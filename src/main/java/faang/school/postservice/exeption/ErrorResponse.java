package faang.school.postservice.exeption;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime localDateTime;
}