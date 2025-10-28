package ru.yandex.practicum.compilation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.event.dto.EventShortDto;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CompilationDto {
    private final List<EventShortDto> events;
    private final Integer id;
    private final Boolean pinned;
    private final String title;
}
