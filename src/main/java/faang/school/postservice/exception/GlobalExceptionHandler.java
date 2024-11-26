package faang.school.postservice.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import faang.school.postservice.constant.ValidationConstant;
import faang.school.postservice.dto.error.InvalidFormatResponseDto;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleCommentNotFoundException(ValidationException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());
        log.error("ApiException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(body, exception.getHttpStatus());
    }

    @ExceptionHandler(InputValidationException.class)
    public ResponseEntity<?> handleInputValidationErrors(InputValidationException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .errorFields(ex.getErrorFields())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<?> handleExternalException(ExternalServiceException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(ex.getMessage())
                        .build(),
                ex.getStatus()
        );
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignServiceUnavailableException(FeignException ex) {
        String message;

        if (ex.getCause() instanceof ConnectException) {
            message = "Connection to external service failed";
        } else {
            message = ex.getMessage();
        }
        log.error("FeignException occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(message)
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(ex.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public InvalidFormatResponseDto handleInvalidFormatException(InvalidFormatException e) {
        log.error("InvalidFormatException occurred: {}", e.getMessage(), e);
        String fieldName = e.getPath().get(0).getFieldName();
        return new InvalidFormatResponseDto(ValidationConstant.INVALID_FORMAT, fieldName, getExpectedFormat(fieldName));
    }

    private String getExpectedFormat(String fieldName) {
        return switch (fieldName) {
            case "createdAt", "updatedAt" -> ValidationConstant.DATE_FORMAT;
            default -> {
                log.warn("Unexpected field name: {}", fieldName);
                yield ValidationConstant.UNKNOWN_FORMAT;
            }
        };
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> errors = bindingResult.getFieldErrors();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorFields(new HashMap<>())
                .build();
        errors.forEach(
                error -> errorResponse.addError(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
