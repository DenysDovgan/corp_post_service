package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.LikeKafkaEvent;
import faang.school.postservice.model.redis.LikeRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final RedisPostRepository redisPostRepository;
    private final ExpirableLockRegistry lockRegistry;

    @Value("${spring.data.redis.lock-key}")
    private String redisLockKey;

    @KafkaListener(topics = "${spring.data.kafka.topics.likes.name}", groupId = "${spring.data.kafka.consumer.group-id}")
    public void listenLikeEvent(LikeKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Like event received. Author ID: {}, Post ID: {}", event.getAuthorId(), event.getPostId());
        PostRedis foundPost = redisPostRepository.findById(event.getPostId()).orElse(null);
        if (foundPost != null) {
            Lock lock = lockRegistry.obtain(redisLockKey);
            if (lock.tryLock()) {
                try {
                    LikeRedis like = LikeRedis.builder()
                            .userId(event.getAuthorId())
                            .build();
                    if (foundPost.getLikes() == null) {
                        foundPost.setLikes(List.of(like));
                    } else {
                        foundPost.getLikes().add(like);
                    }
                    redisPostRepository.save(foundPost);
                } finally {
                    lock.unlock();
                }
            }
        }
        acknowledgment.acknowledge();
    }
}