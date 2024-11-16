package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.topic.CommentEventTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommentEventPublisher implements MessagePublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final CommentEventTopic topic;
    private final ObjectMapper objectMapper;

    public CommentEventPublisher(@Qualifier("redisPubSubTemplate") RedisTemplate<String, String> redisTemplate,
                                 CommentEventTopic topic,
                                 ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(topic.getTopic(), json);
            log.info("published: {}", json);
        } catch (JsonProcessingException e) {
            log.error("not converted to json: {}", message, e);
        }
    }
}
