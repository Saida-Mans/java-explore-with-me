package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends RuntimeException {

    private final HttpStatus status;
    private final String error;

    public ValidationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.error = message;
    }
}
