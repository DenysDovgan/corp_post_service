package faang.school.postservice.dto;

import faang.school.postservice.model.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigInteger;

@Builder
public record ResourceDto(
        Long id,
        @Positive long postId,
        @NotBlank ResourceType type,
        @Positive BigInteger size
) {
}