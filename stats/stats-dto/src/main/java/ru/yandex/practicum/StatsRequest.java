package ru.yandex.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class StatsRequest {
    LocalDateTime start;
    LocalDateTime end;
    List<String> uris;
    boolean unique;
}
