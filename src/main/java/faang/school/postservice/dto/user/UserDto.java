package faang.school.postservice.dto.user;

import jakarta.validation.constraints.NotNull;

public record UserDto(
        @NotNull Long id,
        @NotNull String username
) {

}
