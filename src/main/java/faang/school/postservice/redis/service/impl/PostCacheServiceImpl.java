package faang.school.postservice.redis.service.impl;

import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.event.kafka.CommentEventKafka;
import faang.school.postservice.model.event.kafka.PostEventKafka;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.dto.CommentRedisDto;
import faang.school.postservice.redis.model.entity.PostCache;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.redis.service.FeedCacheService;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PostCacheServiceImpl implements PostCacheService {

    @Value("${cache.post-ttl}")
    private long postTtl;

    @Value("${cache.post.fields.views}")
    private String postCacheViewsField;

    @Value("${cache.post.prefix}")
    private String cachePrefix;

    @Value("${post-comments.size}")
    private int postCommentsSize;

    private final PostCacheRedisRepository postCacheRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheMapper postCacheMapper;
    private final RedissonClient redissonClient;
    private final FeedCacheService feedCacheService;

    @Autowired
    public PostCacheServiceImpl(PostCacheRedisRepository postCacheRedisRepository,
                                @Qualifier("redisCacheTemplate") RedisTemplate<String, Object> redisTemplate,
                                PostCacheMapper postCacheMapper, RedissonClient redissonClient, FeedCacheService feedCacheService) {
        this.postCacheRedisRepository = postCacheRedisRepository;
        this.redisTemplate = redisTemplate;
        this.postCacheMapper = postCacheMapper;
        this.redissonClient = redissonClient;
        this.feedCacheService = feedCacheService;
    }

    @Override
    public void savePostToCache(PostDto post) {
        log.info("Saving post with ID {} to cache", post.getId());

        PostCache postCache = postCacheMapper.toPostCache(post);
        postCacheRedisRepository.save(postCache);

        String key = createPostCacheKey(post.getId());

        redisTemplate.expire(key, Duration.ofSeconds(postTtl));

        log.info("Post with ID {} saved to cache with key: {} and TTL: {} seconds", post.getId(), key, postTtl);
    }

    @Override
    public void addPostView(PostDto post) {
        String lockKey = "lock:" + post.getId();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            log.debug("Lock acquired for postId: {}", post.getId());
            incrementNumberOfPostViews(post.getId());
            log.info("Successfully incremented views for postId: {}", post.getId());
        } finally {
            lock.unlock();
            log.debug("Lock released for postId: {}", post.getId());
        }
    }


    private void incrementNumberOfPostViews(Long postId) {
        redisTemplate.opsForHash()
                .increment(createPostCacheKey(postId), String.valueOf(postCacheViewsField), 1);
    }

    private String createPostCacheKey(Long postId) {
        return cachePrefix + postId;
    }

    @Override
    public void updatePostComments(CommentEventKafka event) {
        PostCache postCache = postCacheRedisRepository.findById(event.getPostId())
                .orElseThrow(() -> new NoSuchElementException("Can't find post in redis with id: " + event.getPostId()));

        String lockKey = "lock:" + event.getPostId();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();

        try {
            TreeSet<CommentRedisDto> postComments = postCache.getComments();
            CommentRedisDto commentRedisDto = CommentRedisDto.builder()
                    .id(event.getCommentId())
                    .postId(event.getPostId()).content(event.getContent())
                    .createdAt(event.getCreatedAt()).authorId(event.getAuthorId()).build();
            if (postComments == null) {
                postComments = new TreeSet<>();
            } else if (postComments.size() == postCommentsSize) {
                postComments.remove(postComments.last());
            }
            postComments.add(commentRedisDto);
            postCache.setComments(postComments);
            postCacheRedisRepository.save(postCache);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateFeedsInCache(PostEventKafka event) {
        List<CompletableFuture<Void>> features = event.getFollowerIds().stream()
                .map(followerId -> feedCacheService.getAndSaveFeed(followerId, event.getPostId()))
                .toList();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(features.toArray(new CompletableFuture[0]));
        allFutures.join();
    }

    @Override
    public CompletableFuture<Void> saveAllPostsToCache(List<PostDto> posts) {

        List<PostCache> newPostCaches = filterNewPosts(posts).stream()
                .map(postCacheMapper::toPostCache)
                .toList();

        if (!newPostCaches.isEmpty()) {
            log.info("Saving {} new posts to cache.", newPostCaches.size());
            return CompletableFuture.runAsync(() -> {
                postCacheRedisRepository.saveAll(newPostCaches);
                setTtlForPosts(newPostCaches);
            });
        }

        log.info("No new posts to cache.");
        return CompletableFuture.completedFuture(null);
    }

    private List<PostDto> filterNewPosts(List<PostDto> posts) {
        List<String> keys = posts.stream()
                .map(post -> "post:" + post.getId())
                .toList();

        List<Object> results = redisTemplate.opsForValue().multiGet(keys);

        if (results == null) {
            log.warn("Failed to retrieve keys from Redis. Assuming all posts are new.");
            return posts;
        }

        return posts.stream()
                .filter(user -> {
                    int index = posts.indexOf(user);
                    return results.get(index) == null;
                })
                .toList();
    }

    private void setTtlForPosts(List<PostCache> newPostCaches) {
        newPostCaches.forEach(postCache -> {
            String key = createPostCacheKey(postCache.getId());
            redisTemplate.expire(key, Duration.ofSeconds(postTtl));
            log.info("Set TTL for post {} in cache.", postCache.getId());
        });
    }
}