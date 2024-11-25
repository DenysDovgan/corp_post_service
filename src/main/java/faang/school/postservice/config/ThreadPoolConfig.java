package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Value("${post.thread-pool.count_pool}")
    private int publishedPostCountPool;

    @Value("${feed.thread.pool-size}")
    private int feedPoolSize;

    @Bean
    public ExecutorService publishedPostThreadPool() {
        return Executors.newFixedThreadPool(publishedPostCountPool);
    }

    @Bean(name = "feedThreadPool")
    public ExecutorService feedThreadPool() {
        return Executors.newFixedThreadPool(feedPoolSize);
    }
}