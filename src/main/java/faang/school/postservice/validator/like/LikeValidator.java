package faang.school.postservice.validator.like;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.entity.Likeable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeValidator {

    public <T extends Likeable, R extends JpaRepository<T, Long>> T validate(long commentAndPostId, Long userId, R repository) {
        T postAndComment = validateCommentOrPost(commentAndPostId, repository);
        checkingExistingLike(postAndComment, userId);
        return postAndComment;
    }

    public <T, R extends JpaRepository<T, Long>> T validateCommentOrPost(long commentAndPostId, R repository) {
        return repository.findById(commentAndPostId).orElseThrow(() -> {
            log.error("The comment with ID {}  does not exist", commentAndPostId);
            return new DataValidationException("The comment does not exist " + commentAndPostId);
        });
    }

    public <T extends Likeable> void checkingExistingLike(T commentOrPost, Long userId) {
        commentOrPost.getLikes().stream()
                .filter(like -> like.getUserId().equals(userId))
                .findFirst()
                .ifPresent(like -> {
                    log.error("User with ID {} already liked the {} with ID {}",
                            userId,
                            commentOrPost.getClass().getName(),
                            commentOrPost.getId());
                    throw new DataValidationException("User with ID " +
                            userId +
                            " already liked the" +
                            commentOrPost.getClass().getName() +
                            " with ID " +
                            commentOrPost.getId());
                });
    }
}
