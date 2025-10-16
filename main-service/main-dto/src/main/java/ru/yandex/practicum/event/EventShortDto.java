package ru.yandex.practicum.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.category.CategoryDto;
import ru.yandex.practicum.user.UserShortDto;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private CategoryDto categoryDto;
    private int confirmedRequests;
    private String eventDate;
    private int id;
    private UserShortDto userShortDto;
    private boolean paid;
    private String title;
    private int views;
}
