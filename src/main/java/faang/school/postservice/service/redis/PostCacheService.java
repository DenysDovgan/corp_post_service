package faang.school.postservice.service.redis;

import faang.school.postservice.dto.comment.LastCommentDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static faang.school.postservice.converters.CollectionConverter.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCacheService {
    private final PostService postService;
    @Value("${spring.data.redis.cache.post_topic.ttl}")
    private long ttl;
    @Value("${news-feed.cache.suffix.post}")
    private String postSuffix;
    @Value("${news-feed.feed.max_retries}")
    private int maxAttempts;
    @Value("${news-feed.feed.max_comments}")
    private int maxComments;
    private final PostCacheRepository postRepository;
    private final PostMapper postMapper;
    private final UserCacheService userCacheService;
    private final RedisTransactionsService redisTransactionsService;
    private final PostCacheUtilService postCacheUtilService;

    public RedisPost findPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found"));
    }

    public List<RedisPost> findPosts(List<Long> ids) {
        return toList(postRepository.findAllById(ids));
    }

    public void addPost(Post post) {
        log.info("publishing post {} to post cache", post.getId());
        RedisPost redisPost = postMapper.toRedisEntity(post);
//        String postAuthorName = getUserName(post.getAuthorId());
//
//        redisPost.getPostInfoDto().getDto().setUsername(postAuthorName);
//        LinkedHashSet<LastCommentDto> lastComments = redisPost.getPostInfoDto().getComments();
//        lastComments = lastComments.stream().peek(comment -> {
//            String commentAuthorName;
//            commentAuthorName = getUserName(comment.getAuthorId());
//            comment.setAuthor(commentAuthorName);
//        }).collect(Collectors.toCollection(LinkedHashSet::new));
//        redisPost.getPostInfoDto().setComments(lastComments);
        savePost(postCacheUtilService.getFilledRedisPost(redisPost));
    }

    private void savePost(RedisPost redisPost) {
        redisPost.setTimeToLive(ttl);
        log.info("saving post {} to post cache with ttl = {}", redisPost.getId(), ttl);
        Optional<RedisPost> postOptional = postRepository.findById(redisPost.getId());
        if (postOptional.isPresent()) {
            log.info("post {} already exists", redisPost.getId());
        } else {
            postRepository.save(redisPost);
            log.info("post {} saved", redisPost.getId());
        }
    }

    public List<RedisPost> findUserPosts(Long userId, int amount) {
//        String postAuthorName = postCacheUtilService.getUserName(userId);
        return postService.getUserPosts(userId, amount)
                .stream()
                .map(postMapper::toRedisEntity).toList();
//        return postService.getUserPosts(userId, amount)
//                .stream()
//                .map(post -> {
//                    RedisPost redisPost = postMapper.toRedisEntity(post);
//                    redisPost.getPostInfoDto().getDto().setUsername(postAuthorName);
//                    postCacheUtilService.setLastComments(redisPost);
//                    return redisPost;
//                })
//                .toList();
    }

    public boolean managePostsAndSave(Long userId, List<RedisPost> posts) {
        String postAuthorName = postCacheUtilService.getUserName(userId);
        List<RedisPost> list = CompletableFuture.supplyAsync(() -> posts.stream()
                .peek(redisPost -> {
                    redisPost.getPostInfoDto().getDto().setUsername(postAuthorName);
                    postCacheUtilService.setLastComments(redisPost);
                })
                .toList()).join();
        return savePosts(list);
    }

    public boolean savePosts(List<RedisPost> redisPosts) {
        List<RedisPost> posts = toList(postRepository.saveAll(redisPosts));
        if (!posts.isEmpty()) {
            log.info("{} posts saved to redis cache", redisPosts.size());
            return true;
        } else {
            log.warn("{} posts saved to redis cache", redisPosts.size());
            return false;
        }
    }

    public void incrementLike(Long postId) {
        String postKey = postSuffix + ":" + postId;
        BiConsumer<RedisOperations<String, RedisPost>, String> incrLike = (operation, key) -> {
            RedisPost redisPost = operation.opsForValue().get(postId);
            if (redisPost != null) {
                log.info("got post {} info to increment like", postId);
                long likes = redisPost.getPostInfoDto().getLikes();
                likes++;
                redisPost.getPostInfoDto().setLikes(likes);
                postRepository.save(redisPost);
            }
        };
        redisTransactionsService.implementOperation(postKey, new RedisPost(), maxAttempts, incrLike);
    }

    public void incrementPostView(Long postId) {
        String postKey = postSuffix + ":" + postId;
        BiConsumer<RedisOperations<String, RedisPost>, String> incrView = (operation, key) -> {
            RedisPost redisPost = operation.opsForValue().get(postId);
            if (redisPost != null) {
                log.info("got post {} info to increment view", postId);
                long views = redisPost.getPostInfoDto().getViews();
                views++;
                redisPost.getPostInfoDto().setViews(views);
                postRepository.save(redisPost);
            }
        };
        redisTransactionsService.implementOperation(postKey, new RedisPost(), maxAttempts, incrView);
    }

    public void addCommentToPost(Long postId, LastCommentDto lastCommentDto) {
        String postKey = postSuffix + ":" + postId;
        BiConsumer<RedisOperations<String, RedisPost>, String> addComment = (operation, key) -> {
            RedisPost redisPost = operation.opsForValue().get(postId);
            if (redisPost != null) {
                log.info("got post {} info to increment comment", postId);
                LinkedHashSet<LastCommentDto> lastCommentsList = redisPost.getPostInfoDto().getComments();
                if (lastCommentsList.size() < maxComments) {
                    lastCommentsList.add(lastCommentDto);
                    redisPost.getPostInfoDto().setComments(lastCommentsList);
                    postRepository.save(redisPost);
                    log.info("comment added to post {} with new comment {}", postId, lastCommentDto.getComment());
                } else {
                    List<LastCommentDto> list = new ArrayList<>(lastCommentsList);
                    LastCommentDto lastComment = list.get(list.size() - 1);
                    lastCommentsList.remove(lastComment);
                    LinkedHashSet<LastCommentDto> newComments = new LinkedHashSet<>();
                    newComments.add(lastCommentDto);
                    newComments.addAll(lastCommentsList);
                    lastCommentsList.clear();
                    lastCommentsList.addAll(newComments);
                    redisPost.getPostInfoDto().setComments(lastCommentsList);
                    postRepository.save(redisPost);
                }
            } else {
                log.warn("post {} was not found", postId);
            }
        };
        redisTransactionsService.implementOperation(postKey, new RedisPost(), maxAttempts, addComment);
    }
}
