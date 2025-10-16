package ru.yandex.practicum.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewCategoryDto {
    @NotBlank
    private String name;
}
