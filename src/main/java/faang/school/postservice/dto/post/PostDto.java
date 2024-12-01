package faang.school.postservice.dto.post;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Validated
public record PostDto(
        @Nullable Long id,
        @NotBlank String content,
        @Nullable Long authorId,
        @Nullable Long projectId,
        @Nullable List<Long> likesIds,
        @Nullable List<Long> commentsIds,
        @NotNull boolean published,
        @NotNull boolean deleted,
        @Nullable LocalDateTime publishedAt
) {}
