package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.properties.BatchProperties;
import faang.school.postservice.dto.post.message.PostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.kafkaProducer.PostEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SenderBatchesPostEvent {

    private final UserServiceClient userServiceClient;
    private final PostEventProducer postEventProducer;
    private final BatchProperties batchProperties;

    @Async("customExecutor")
    public void batchSending(Post post) {
        List<Long> userSubscribersIds = userServiceClient.getUserSubscribers(post.getAuthorId());
        log.info("Received {} subscribers for post {}", userSubscribersIds.size(), post.getId());

        if (userSubscribersIds.isEmpty()) {
            log.warn("No subscribers found for post's author: {}", post.getAuthorId());
            return;
        }

        int totalBatches = (int) Math.ceil((double) userSubscribersIds.size() / batchProperties.getBatchSizeSubscribers());

        for (int batchNumber = 1; batchNumber <= totalBatches; batchNumber++) {
            int start = (batchNumber - 1) * batchProperties.getBatchSizeSubscribers();
            int end = Math.min(start + batchProperties.getBatchSizeSubscribers(), userSubscribersIds.size());

            List<Long> batch = userSubscribersIds.subList(start, end);
            buildAndSendEvent(post, batch);
        }
    }

    private void buildAndSendEvent(Post post, List<Long> userSubscribersIds) {
        log.info("Sending batch for post {}, batch size: {}", post.getId(), userSubscribersIds.size());

        PostEvent postEvent = PostEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .subscribers(userSubscribersIds)
                .build();
        log.debug("PostEvent is created: {}", postEvent.toString());

        try {
            postEventProducer.sendEvent(postEvent);
            log.debug("PostEvent has been sent to Kafka topic: {}", postEvent);
        } catch (Exception ex) {
            log.error("Failed to send postEvent for postId {}: {}", postEvent.toString(), ex.getMessage());
        }
    }
}