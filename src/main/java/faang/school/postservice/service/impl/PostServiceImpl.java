package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.kafka.producer.PostProducer;
import faang.school.postservice.mapper.RedisPostDtoMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserWithFollowersMapper;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.ProjectDto;
import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.model.dto.UserWithFollowersDto;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.entity.UserShortInfo;
import faang.school.postservice.model.enums.AuthorType;
import faang.school.postservice.model.event.PostViewEvent;
import faang.school.postservice.model.event.kafka.PostPublishedEvent;
import faang.school.postservice.redis.publisher.NewPostPublisher;
import faang.school.postservice.redis.publisher.PostViewPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserShortInfoRepository;
import faang.school.postservice.service.BatchProcessService;
import faang.school.postservice.service.PostBatchService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.RedisPostService;
import faang.school.postservice.service.RedisUserService;
import faang.school.postservice.util.moderation.ModerationDictionary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private static final int REFRESH_TIME_IN_HOURS = 3;

    @Value("${spell-checker.batch-size}")
    private int correcterBatchSize;

    @Value("${kafka.batch-size.follower:1000}")
    private int followerBatchSize;

    @Value("${post.publisher.batch-size}")
    private int batchSize;

    @Value("${post.moderation.batch-size}")
    private int moderationBatchSize;

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;
    private final NewPostPublisher newPostPublisher;
    private final ModerationDictionary moderationDictionary;
    private final BatchProcessService batchProcessService;
    private final ExecutorService schedulingThreadPoolExecutor;
    private final PostBatchService postBatchService;
    private final PostViewPublisher postViewPublisher;
    private final UserContext userContext;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PostProducer postProducer;
    private final UserShortInfoRepository userShortInfoRepository;
    private final RedisUserService redisUserService;
    private final UserWithFollowersMapper userWithFollowersMapper;
    private final RedisPostDtoMapper redisPostDtoMapper;
    private final RedisPostService redisPostService;

    @Override
    public PostDto createPost(PostDto postDto) {
        if (postDto.getAuthorType() == AuthorType.USER) {
            UserDto user = userServiceClient.getUser(postDto.getAuthorId());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
        } else if (postDto.getAuthorType() == AuthorType.PROJECT) {
            ProjectDto project = projectServiceClient.getProject(postDto.getAuthorId());
            if (project == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
            }
        } else {
            throw new IllegalArgumentException("Invalid author type");
        }

        Post post = postMapper.toPost(postDto);
        Post savedPost = postRepository.save(post);
        PostDto result = postMapper.toPostDto(savedPost);

        newPostPublisher.publish(result);
        return result;
    }

    @Override
    @Transactional
    public PostDto publishPost(Long id) {
        Post post = getPostById(id);

        if (post.isPublished()) {
            throw new IllegalStateException("Post is already published");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        log.debug("Saving author of post (user with id = {}) in DB and Redis", post.getAuthorId());
        updateUserShortInfoIfStale(post, REFRESH_TIME_IN_HOURS);

        log.debug("Saving post with id = {} in DB and Redis", post.getId());
        Post savedPost = postRepository.save(post);
        PostDto postDto = postMapper.toPostDto(savedPost);
        RedisPostDto redisPostDto = redisPostDtoMapper.mapToRedisPostDto(postDto);
        redisPostService.savePostIfNotExists(redisPostDto);

        log.debug("Start sending PostPublishedEvent for post with id = {} to Kafka", post.getId());
        List<Long> followerIds = redisUserService.getFollowerIds(post.getAuthorId());
        PostPublishedEvent postPublishedEvent = new PostPublishedEvent(postDto.getId(), followerIds, postDto.getPublishedAt());
        applicationEventPublisher.publishEvent(postPublishedEvent);
        return postDto;
    }
    //TODO в Redis тоже должно публиковаться после комита в БД
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRequestCompleted(PostPublishedEvent postPublishedEvent) {
        List<Long> followerIds = postPublishedEvent.getFollowerIds();

        if (followerIds.isEmpty()) {
            return;
        }

        for (int indexFrom = 0; indexFrom < followerIds.size(); indexFrom += followerBatchSize) {
            int indexTo = Math.min(indexFrom + followerBatchSize, followerIds.size());
            PostPublishedEvent subEvent = new PostPublishedEvent(
                    postPublishedEvent.getPostId(),
                    followerIds.subList(indexFrom, indexTo),
                    postPublishedEvent.getPublishedAt());
            postProducer.sendEvent(subEvent);
        }
    }

    @Override
    public PostDto updatePost(Long id, PostDto postDto) {
        Post post = getPostById(id);

        if (!post.getAuthorId().equals(postDto.getAuthorId()) || !postDto.getAuthorType().equals(postDto.getAuthorType())) {
            throw new IllegalStateException("Cannot change author or author type of the post");
        }

        post.setContent(postDto.getContent());
        postRepository.save(post);

        return postMapper.toPostDto(post);
    }

    @Override
    public void deletePost(Long id) {
        Post post = getPostById(id);
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    public PostDto getPost(Long id) {
        Post post = getPostById(id);
        postViewPublisher.publish(createPostViewEvent(post));
        return postMapper.toPostDto(post);
    }

    @Override
    public List<PostDto> getUserDrafts(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getProjectDrafts(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getUserPublishedPosts(Long authorId) {
        List<PostDto> dtos = postRepository.findByAuthorIdWithLikes(authorId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());

        if (!dtos.isEmpty()) {
            dtos.forEach(postDto -> postViewPublisher.publish(createPostViewEvent(postDto)));

        }

        return dtos;
    }

    @Override
    public List<PostDto> getProjectPublishedPosts(Long projectId) {
        List<PostDto> dtos = postRepository.findByProjectIdWithLikes(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());

        if (!dtos.isEmpty()) {
            dtos.forEach(postDto -> postViewPublisher.publish(createPostViewEvent(postDto)));
        }

        return dtos;
    }

    private Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDto> getAllPostsByHashtagId(String content, Pageable pageable) {
        Page<PostDto> pagesDtos = postRepository.findByHashtagsContent(content, pageable).map(postMapper::toPostDto);
        if (pagesDtos.getSize() > 0) {
            pagesDtos.getContent().forEach(postDto -> postViewPublisher.publish(createPostViewEvent(postDto)));
        }
        return pagesDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPostByIdInternal(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("'Post not in database' error occurred while fetching post"));
        postViewPublisher.publish(createPostViewEvent(post));

        return post;
    }

    @Override
    @Transactional
    public Post updatePostInternal(Post post) {
        return postRepository.save(post);
    }

    @Override
    public List<CompletableFuture<Void>> publishScheduledPosts() {
        List<Post> readyToPublish = postRepository.findReadyToPublish();
        log.info("{} posts were found for scheduled publishing", readyToPublish.size());
        List<List<Post>> postBatches = partitionList(readyToPublish, batchSize);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Post> postBatch : postBatches) {
            postBatch.forEach(post -> {
                post.setPublished(true);
                post.setPublishedAt(LocalDateTime.now());
                log.info("Post with id '{}' prepared for scheduled publishing", post.getId());
            });
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> postBatchService.savePostBatch(postBatch), schedulingThreadPoolExecutor);
            futures.add(future);
        }
        return futures;
    }

    private List<List<Post>> partitionList(List<Post> list, int batchSize) {
        List<List<Post>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }


    @Transactional
    public void correctSpellingInUnpublishedPosts() {
        List<Post> unpublishedPosts = postRepository.findReadyForSpellCheck();

        if (!unpublishedPosts.isEmpty()) {
            int batchSize = correcterBatchSize;
            List<List<Post>> batches = partitionList(unpublishedPosts, batchSize);

            List<CompletableFuture<Void>> futures = batches.stream()
                    .map(batchProcessService::processBatch)
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }

    @Override
    public List<List<Post>> findAndSplitUnverifiedPosts() {
        List<Post> unverifiedPosts = postRepository.findAllByVerifiedDateIsNull();

        return partitionList(unverifiedPosts, moderationBatchSize);
    }

    @Override
    @Async("postOperationsAsyncExecutor")
    @Transactional
    public CompletableFuture<Void> verifyPostsForSwearWords(List<Post> unverifiedPostsBatch) {
        return CompletableFuture.runAsync(() -> {
            unverifiedPostsBatch.forEach(post -> {
                boolean hasImproperContent = moderationDictionary.containsSwearWords(post.getContent());
                post.setVerified(!hasImproperContent);
                post.setVerifiedDate(LocalDateTime.now());
            });

            postRepository.saveAll(unverifiedPostsBatch);
        });
    }

    private PostViewEvent createPostViewEvent(Post post) {
        return new PostViewEvent(post.getId(), post.getAuthorId(), userContext.getUserId(), LocalDateTime.now());
    }

    private PostViewEvent createPostViewEvent(PostDto post) {
        return new PostViewEvent(post.getId(), post.getAuthorId(), userContext.getUserId(), LocalDateTime.now());
    }

    private void updateUserShortInfoIfStale(Post post, int refreshTimeInHours) {
        Optional<LocalDateTime> lastSavedAt = userShortInfoRepository.findLastSavedAtByUserId(post.getAuthorId());
        if (lastSavedAt.isEmpty() || lastSavedAt.get().isBefore(LocalDateTime.now().minusHours(refreshTimeInHours))) {
            UserWithFollowersDto userWithFollowers = userServiceClient.getUserWithFollowers(post.getAuthorId());
            UserShortInfo userShortInfo = userWithFollowersMapper.toUserShortInfo(userWithFollowers);
            userShortInfoRepository.save(userShortInfo);
            redisUserService.saveUser(userWithFollowersMapper.toRedisUserDto(userWithFollowers));
        }
    }
}
