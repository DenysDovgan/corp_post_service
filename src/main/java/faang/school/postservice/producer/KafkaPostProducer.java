package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEventKafka;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${spring.kafka.topics.post}")
    private String topicPost;

    @Async("executor")
    public void sendMessage(PostEventKafka postEventKafka) {
        String msg = null;
        try {
            msg = new ObjectMapper().writeValueAsString(postEventKafka);
        } catch (JsonProcessingException e) {
            log.error("Failed to make JSON");
            throw new RuntimeException(Arrays.toString(e.getStackTrace()));
        }
        kafkaTemplate.send(topicPost, msg);
    }
}
