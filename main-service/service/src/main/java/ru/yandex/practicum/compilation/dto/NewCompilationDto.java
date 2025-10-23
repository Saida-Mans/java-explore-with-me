package ru.yandex.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private Set<Integer> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Длина названия подборки от 1 до 50", groups = {OnPost.class, OnPatch.class})
    @NotBlank(message = "У подборки должно быть заглавие !", groups = OnPost.class)
    private String title;

    public interface OnPost {}

    public interface OnPatch {}
}
