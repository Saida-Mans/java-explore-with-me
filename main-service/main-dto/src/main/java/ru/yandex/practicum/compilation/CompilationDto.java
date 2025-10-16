package ru.yandex.practicum.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.event.EventShortDto;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private int id;
    private boolean pinned;
    private String title;
    private List<EventShortDto> events;
}
