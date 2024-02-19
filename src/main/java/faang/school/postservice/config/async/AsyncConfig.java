package faang.school.postservice.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public ExecutorService asyncExecutor() {
        return new ThreadPoolExecutor(5, 20, 3, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000));
    }
}
