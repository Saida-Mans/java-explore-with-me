package ru.yandex.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class StatsRequest {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uris;
    private boolean unique;
}
