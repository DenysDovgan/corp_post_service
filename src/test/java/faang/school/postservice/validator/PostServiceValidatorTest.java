package faang.school.postservice.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostServiceValidator validator;

    @Test
    public void testCheckPublicationPost() {
        long postId = 1L;
        Post post = Post.builder().id(postId).published(true).build();
        String message = "Пост с id 1 уже опубликован";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.checkPublicationPost(post));
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    public void testValidateDeleteAuthor() {
        PostDto newPost = new PostDto(null, null, null, null, null, null, false, false, null);
        Post oldPost = Post.builder().build();
        String message = "Нельзя удалять автора поста";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateAuthorsEquals(oldPost, newPost));
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    public void testValidateEqualsAuthors() {
        Post oldPost = Post.builder().projectId(10L).authorId(null).build();
        PostDto newPost = new PostDto(null, null, null, 11L, null, null, false, false, null);
        String message = "Нельзя менять автора поста";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateAuthorsEquals(oldPost, newPost));
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    public void testValidateAuthorId() {
        long id = 1L;
        String message = "Превышен лимит запросов в UserService: null";
        when(userServiceClient.getUser(id)).thenThrow(FeignException.class);

        FeignException exception = assertThrows(FeignException.class,
                () -> validator.validateAuthorId(id));
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> validator.getUserRecover(exception, id));
        assertThat(dataValidationException.getMessage()).isEqualTo(message);
    }

    @Test
    public void testValidateProjectId() {
        long id = 1L;
        String message = "Превышен лимит запросов в ProjectService: null";
        when(projectServiceClient.getProject(id)).thenThrow(FeignException.class);

        FeignException exception = assertThrows(FeignException.class,
                () -> validator.validateProjectId(id));
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> validator.getProjectRecover(exception, id));
        assertThat(dataValidationException.getMessage()).isEqualTo(message);
    }
}