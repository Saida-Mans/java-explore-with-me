package ru.yandex.practicum.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String name;
}
