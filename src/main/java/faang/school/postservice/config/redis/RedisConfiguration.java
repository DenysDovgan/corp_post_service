package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public ChannelTopic likePostEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getLikePostChannel().getName());
    }

    @Bean
    public ChannelTopic postViewEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPostViewChannel().getName());
    }

    @Bean
    public ChannelTopic commentEventChannel() {
        return new ChannelTopic(redisProperties.getChannels().getCommentChannel().getName());
    }

    @Bean
    public ChannelTopic achievementEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getLikePostChannel().getName());
    }

    @Bean
    public ChannelTopic publishedCommentEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getNewCommentChannel().getName());
    }

    @Bean
    ChannelTopic userBanTopic() {
        return new ChannelTopic(redisProperties.getChannels().getUserBanChannel().getName());
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration
                .defaultCacheConfig(Thread.currentThread().getContextClassLoader())
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
    }
}
