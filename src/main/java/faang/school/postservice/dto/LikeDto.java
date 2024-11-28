package faang.school.postservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LikeDto(
        long id,
        Long userId,
        Long authorId,
        Long idComment,
        Long idPost
) {
}