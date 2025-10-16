package ru.yandex.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.event.State;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AdminEventSearchParams {
    private List<Long> users;
    private List<State> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private int from;
    private int size;
}
