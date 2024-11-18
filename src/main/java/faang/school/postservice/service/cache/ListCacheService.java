package faang.school.postservice.service.cache;

import java.util.Optional;

public interface ListCacheService<T> {

    void put(String listKey, T value);

    Optional<T> leftPop(String listKey, Class<T> clazz);

    long size(String key);

    void runInOptimisticLock(Runnable task);
}
