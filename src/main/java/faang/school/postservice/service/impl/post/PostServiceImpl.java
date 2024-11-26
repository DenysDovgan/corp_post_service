package faang.school.postservice.service.impl.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.dto.post.PostDto;
import faang.school.postservice.model.dto.user.UserDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.model.event.kafka.PostNFEvent;
import faang.school.postservice.model.event.kafka.PostObservationEvent;
import faang.school.postservice.publisher.kafka.KafkaPostProducer;
import faang.school.postservice.publisher.kafka.KafkaPostViewProducer;
import faang.school.postservice.publisher.kafka.PostEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.HashtagService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.PostServiceAsync;
import faang.school.postservice.service.RedisCacheService;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final HashtagService hashtagService;
    private final PostValidator postValidator;
    private final PostServiceAsync postServiceAsync;
    private final PostEventPublisher postEventPublisher;
    private final KafkaPostProducer kafkaPostProducer;
    private final UserServiceClient userServiceClient;
    private final KafkaPostViewProducer kafkaPostViewProducer;
    private final RedisCacheService redisCacheService;

    @Value("${post.correcter.posts-batch-size}")
    private int batchSize;

    @Override
    @Transactional
    public PostDto createDraftPost(PostDto postDto) {
        postValidator.createDraftPostValidator(postDto);

        Post post = postMapper.toEntity(postDto);

        post.setPublished(false);

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostDto publishPost(PostDto postDto) {
        Post post = getPostFromRepository(postDto.id());
        postValidator.publishPostValidator(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        Post publishedPost = postRepository.save(post);

        hashtagService.createHashtags(post);
        UserDto author = userServiceClient.getUser(publishedPost.getAuthorId());

        PostEvent event = PostEvent.builder()
                .authorId(publishedPost.getAuthorId())
                .postId(publishedPost.getId())
                .build();
        sendPostEventForNewsFeed(publishedPost);
        postEventPublisher.publish(event);
        redisCacheService.savePost(postMapper.toRedis(publishedPost));
        redisCacheService.saveUser(author);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Post post = getPostFromRepository(postDto.id());

        postValidator.updatePostValidator(post, postDto);

        post.setTitle(postDto.title());
        post.setContent(postDto.content());

        postRepository.save(post);
        hashtagService.updateHashtags(post);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto softDeletePost(Long postId) {
        Post post = getPostFromRepository(postId);

        post.setPublished(false);
        post.setDeleted(true);

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPost(Long id) {
        Post post = getPostFromRepository(id);
        PostObservationEvent observationEvent = new PostObservationEvent(post.getId());
        kafkaPostViewProducer.publish(observationEvent);
        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public List<PostDto> getAllDraftsByAuthorId(Long userId) {
        postValidator.validateIfAuthorExists(userId);

        return postRepository.findAll().stream()
                .filter(post -> Objects.equals(post.getAuthorId(), userId))
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<PostDto> getAllDraftsByProjectId(Long projectId) {
        postValidator.validateIfProjectExists(projectId);

        return postRepository.findAll().stream()
                .filter(post -> Objects.equals(post.getProjectId(), projectId))
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<PostDto> getAllPublishedPostsByAuthorId(Long userId) {
        postValidator.validateIfAuthorExists(userId);

        return postRepository.findAll().stream()
                .filter(post -> Objects.equals(post.getAuthorId(), userId))
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<PostDto> getAllPublishedPostsByProjectId(Long projectId) {
        postValidator.validateIfProjectExists(projectId);

        return postRepository.findAll().stream()
                .filter(post -> Objects.equals(post.getProjectId(), projectId))
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getPostsByHashtag(String hashtag) {
        return hashtagService.findPostsByHashtag(hashtag);
    }

    @Override
    @Transactional
    public void correctUnpublishedPosts() {
        List<Post> postsToCorrect = postRepository.findReadyToPublish();
        ListUtils.partition(postsToCorrect, batchSize)
                .forEach(postServiceAsync::correctUnpublishedPostsByBatches);
    }

    @Override
    public void publishScheduledPosts(int batchSize) {
        var readyToPublishPosts = postRepository.findReadyToPublish();
        ListUtils.partition(readyToPublishPosts, batchSize).forEach(postServiceAsync::publishScheduledPostsAsyncInBatch);
    }

    @Override
    @Transactional
    public void moderatePosts() {
        List<Post> unverifiedPosts = postRepository.findAllByVerifiedDateIsNull();
        List<List<Post>> batches = ListUtils.partition(unverifiedPosts, batchSize);

        batches.forEach(postServiceAsync::moderatePostsByBatches);
    }

    private Post getPostFromRepository(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));
    }

    private void sendPostEventForNewsFeed(Post publishedPost) {
        List<Long> followerIds = userServiceClient.getFollowerIds(publishedPost.getAuthorId());
        PostNFEvent event = PostNFEvent.builder()
                .postId(publishedPost.getId())
                .followersId(followerIds)
                .build();
        kafkaPostProducer.publish(event);
    }
}
