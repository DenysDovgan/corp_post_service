package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostServiceValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void checkPublicationPost(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Пост с id " + post.getId() + " уже опубликован");
        }
    }

    public void validateAuthorsEquals(Post oldPost, PostDto newPost) {
        if (newPost.projectId() == null && newPost.authorId() == null) {
            throw new DataValidationException("Нельзя удалять автора поста");
        }

        if (oldPost.getAuthorId() != null && newPost.authorId() != null && !oldPost.getAuthorId()
                .equals(newPost.authorId()) ||
                oldPost.getProjectId() != null && newPost.projectId() != null && !oldPost.getProjectId()
                        .equals(newPost.projectId())) {
            throw new DataValidationException("Нельзя менять автора поста");
        }
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void validateAuthorId(long id) {
        log.info("Попытка запроса в UserService");
        userServiceClient.getUser(id);
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void validateProjectId(long id) {
        log.info("Попытка запроса в ProjectService");
        projectServiceClient.getProject(id);
    }

    @Recover
    public void getProjectRecover(FeignException ex, Long id) {
        log.error("Превышен лимит запросов в ProjectService c id: {}.", id, ex);
        throw new DataValidationException("Превышен лимит запросов в ProjectService: " + ex.getMessage());
    }

    @Recover
    public void getUserRecover(FeignException ex, Long id) {
        log.error("Превышен лимит запросов в UserService c id: {}.", id, ex);
        throw new DataValidationException("Превышен лимит запросов в UserService: " + ex.getMessage());
    }
}
