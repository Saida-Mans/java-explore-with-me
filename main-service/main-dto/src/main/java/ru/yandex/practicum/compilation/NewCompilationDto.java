package ru.yandex.practicum.compilation;

import lombok.Data;
import java.util.List;

@Data
public class NewCompilationDto {
    private List<Integer> events;
    private boolean pinned;
    private String title;
}
