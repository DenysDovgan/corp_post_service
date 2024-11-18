package faang.school.postservice.listener;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import faang.school.postservice.repository.AsyncCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class KafkaPostListener implements KafkaEventListener<byte[]> {

    private final AsyncCacheRepository<Long> asyncCacheFeedRepository;

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.post-for-feed.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(byte[] byteFeedEvent, Acknowledgment acknowledgment) throws InvalidProtocolBufferException {
        FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.parseFrom(byteFeedEvent);
        List<Long> followerIds = feedEvent.getFollowerIdsList();
        Long postId = feedEvent.getPostId();

        CompletableFuture<?>[] completableFutures = followerIds.stream()
                .map(followerId -> asyncCacheFeedRepository.save(followerId.toString(), postId))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(completableFutures).join();
        acknowledgment.acknowledge();
    }
}
