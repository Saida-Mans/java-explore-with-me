package ru.yandex.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.event.model.Location;
import ru.yandex.practicum.user.dto.UserDto;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventFullDto {
    private final Integer id;
    private final String annotation;
    private final CategoryDto category;
    private final Integer confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdOn;

    private final String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;

    private final UserDto initiator;
    private final Location location;
    private final Boolean paid;
    private final Integer participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime publishedOn;

    private final Boolean requestModeration;
    private final State state;
    private final String title;

    @Setter
    private Long views;
}
