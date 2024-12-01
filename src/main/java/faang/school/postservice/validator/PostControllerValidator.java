package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class PostControllerValidator {

    public void validatePostCreators(PostDto createDto) {
        if (createDto.authorId() != null && createDto.projectId() != null) {
            throw new DataValidationException("У поста не может быть двух авторов");
        }

        if (createDto.authorId() == null && createDto.projectId() == null) {
            throw new DataValidationException("Нет автора поста");
        }
    }

    public void validateId(Long id) {
        if(id == null || id <= 0) {
            throw new DataValidationException("Некорректный id или id равен null");
        }
    }
}
