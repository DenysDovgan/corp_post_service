package faang.school.postservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Object> handleDataValidationException(DataValidationException ex) {
        log.error("Ошибка: ", ex);
        return ResponseEntity.badRequest().body("Сообщение об ошибке: \n" + ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        log.error("Ошибка: ", ex);
        return ResponseEntity.badRequest().body("Непредвиденный RuntimeException: " + ex.getMessage());
    }
}
