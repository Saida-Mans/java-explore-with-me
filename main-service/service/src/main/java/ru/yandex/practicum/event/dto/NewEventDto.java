package ru.yandex.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.event.model.Location;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NewEventDto {

    @Size(min = 20, max = 2000, message = "Длина аннотации от 20 до 2000", groups = {OnPost.class, OnUpdateState.class})
    @NotBlank(message = "У события должна быть аннотация !", groups = {OnPost.class})
    private String annotation;

    @NotNull(message = "У события должно быть наименование категории !", groups = {OnPost.class})
    private Integer category;

    @Size(min = 20, max = 7000, message = "Длина описания от 20 до 7000", groups = {OnPost.class, OnUpdateState.class})
    @NotBlank(message = "У события должно быть описание !", groups = {OnPost.class})
    private String description;

    @NotNull(message = "У события должна быть дата проведения !", groups = {OnPost.class})
    @FutureOrPresent(message = "Дата не может быть в прошлом !", groups = {OnPost.class, OnUpdateState.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "У события должна быть локация проведения !", groups = {OnPost.class})
    private Location location;

    private Boolean paid;

    @Min(value = 0, message = "Минимальное значение количества участников равно 0", groups = {OnPost.class, OnUpdateState.class})
    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Длина заглавия от 3 до 120", groups = {OnPost.class, OnUpdateState.class})
    @NotBlank(message = "Нужно указать заглавие события !", groups = {OnPost.class})
    private String title;

    private Action stateAction;

    public interface OnPost{}

    public interface OnUpdateState {}
}
