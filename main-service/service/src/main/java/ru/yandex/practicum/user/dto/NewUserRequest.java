package ru.yandex.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @Email
    @Size(min = 6, max = 254, message = "Длина почты от 6 до 254")
    @NotBlank(message = "Отсутствует электронная почта !")
    private String email;

    @Size(min = 2, max = 250, message = "Длина имени от 20 до 2000")
    @NotBlank(message = "Отсутствует имя пользователя !")
    private String name;
}
