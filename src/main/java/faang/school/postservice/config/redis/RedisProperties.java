package faang.school.postservice.config.redis;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RedisProperties {
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.channels.user-ban-channel.name}")
    private String userBanChannelName;

    @Value("${spring.data.redis.channels.comment-channel.name}")
    private String commentEventChannelName;

    @Value("${spring.data.redis.channels.post_view_channel.name}")
    private String postViewChannel;

    @Value("${spring.data.redis.channels.post-like-channel.name}")
    private String postLikeEventChannelName;

    @Value("${spring.data.redis.channels.post_channel.name}")
    private String postEventChannelName;
}