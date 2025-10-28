package ru.yandex.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError handleNotFoundException(NotFoundException exc) {
        log.error("Ошибка при поиске: {}", exc.getMessage());
        return new ApiError(HttpStatus.NOT_FOUND,
                "Ошибка при поиске",
                exc.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleValidationException(ValidationException exc) {
        log.error("Ошибка при валидации: {}", exc.getMessage());
        return ResponseEntity
                .status(exc.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiError(exc.getStatus(),
                        "Ошибка при валидации",
                        exc.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError handleConstraintViolationException(ConstraintViolationException exc) {
        log.error("Ошибка при входных данных: {}", exc.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST,
                "Ошибка при входных данных",
                exc.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
        log.error("Ошибка при входных данных: {}", exc.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST,
                "Ошибка при входных данных",
                exc.getMessage(),
                LocalDateTime.now());
    }
}