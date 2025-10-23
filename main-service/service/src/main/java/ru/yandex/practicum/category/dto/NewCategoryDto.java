package ru.yandex.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @Size(min = 1, max = 50, message = "Длина названия категории от 1 до 50")
    @NotBlank(message = "Отсутствует наименование категории !")
    private String name;
}
