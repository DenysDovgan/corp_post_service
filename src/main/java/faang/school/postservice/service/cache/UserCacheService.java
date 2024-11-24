package faang.school.postservice.service.cache;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.cache.CacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService implements SingleCacheService<Long, UserDto> {

    private final CacheRepository<UserDto> cacheRepository;

    @Value("${server.cache.user.count-hours-time-to-live}")
    private int timeToLiveCommentAuthor;

    @Override
    public void save(Long userId, UserDto user) {
        String userKey = createKey(userId);
        Duration timeToLive = Duration.ofHours(timeToLiveCommentAuthor);
        cacheRepository.set(userKey, user, timeToLive);
    }

    @Override
    public UserDto get(Long userId) {
        String userKey = createKey(userId);
        return cacheRepository.get(userKey, UserDto.class).orElseGet(() -> {
            log.warn("Cannot find user with id {}", userId);
            return null;
        });
    }

    private String createKey(Long userId) {
        return userId + "::user";
    }
}
