package ru.yandex.practicum.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String path;
    private String httpMethod;
    private int statusCode;
    private String error;
    private String message;

    public ErrorResponse(String path, String httpMethod, int statusCode, String error, String message) {
        this.path = path;
        this.httpMethod = httpMethod;
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
    }
}
