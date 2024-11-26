package faang.school.postservice.redis.service;

import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.event.kafka.CommentEventKafka;
import faang.school.postservice.model.event.kafka.PostEventKafka;

public interface PostCacheService {

    void savePostToCache(PostDto post);

    void updatePostComments(CommentEventKafka event);

    void addPostView(PostDto post);

    void updateFeedsInCache(PostEventKafka event);
}