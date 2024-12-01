package faang.school.postservice.dto.project;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectDto(
        @Nullable Long id,
        @NotBlank String name,
        @NotBlank String description,
        @NotNull Long ownerId,
        @Nullable List<Long> taskIds,
        @Nullable List<Long> teamIds,
        @Nullable LocalDateTime updatedAt,
        @NotNull ProjectStatusDto status,
        @NotNull ProjectVisibilityDto visibility) {}
