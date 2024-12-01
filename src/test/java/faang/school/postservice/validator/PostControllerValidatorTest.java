package faang.school.postservice.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PostControllerValidatorTest {

    private PostControllerValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new PostControllerValidator();
    }

    @Test
    public void testAllCreatorsAbsents() {
        PostDto postDto = new PostDto(null, "content", null, null, null,
                null, false, false, null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> validator.validatePostCreators(postDto));
        assertThat(exception.getMessage()).isEqualTo("Нет автора поста");
    }

    @Test
    public void testBothCreatorsExists() {
        PostDto postDto = new PostDto(null, "content", 100L, 200L, null,
                null, false, false, null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> validator.validatePostCreators(postDto));
        assertThat(exception.getMessage()).isEqualTo("У поста не может быть двух авторов");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void testValidateIncorrectId(long id) {
        assertThrows(DataValidationException.class, () -> validator.validateId(id));
    }

    @Test
    public void testValidateNullId() {
        assertThrows(DataValidationException.class, () -> validator.validateId(null));
    }
}