package ru.yandex.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.user.dto.UserDto;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventShortDto {
    private final Integer id;

    private final String annotation;

    private final CategoryDto category;

    private final Integer confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;

    private final UserDto initiator;

    private final Boolean paid;

    private final String title;

    @Setter
    private Integer views;
}
