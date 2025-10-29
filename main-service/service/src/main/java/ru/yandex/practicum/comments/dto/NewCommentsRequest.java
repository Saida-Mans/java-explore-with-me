package ru.yandex.practicum.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentsRequest {
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 1, max = 5000, message = "Комментарий должен содержать от {min} до {max} символов")
    private String text;
}
