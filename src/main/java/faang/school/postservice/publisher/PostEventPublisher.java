package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEventDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends AbstractEventPublisher<PostEventDto> {

    public PostEventPublisher(ObjectMapper objectMapper, RedisTemplate redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    private String channelTopicName = "post_achievement_channel";

    public void publish(PostEventDto postEventDto) {
        publishInTopic(postEventDto, channelTopicName);
    }
}
