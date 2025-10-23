package ru.yandex.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;
}
