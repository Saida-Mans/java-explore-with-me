package ru.yandex.practicum.compilation.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.event.dto.EventShortDto;
import java.util.List;

@AllArgsConstructor
@Component
public class CompilationMapper {

    public static CompilationDto compilationToReadDto(Compilation compilation, List<EventShortDto> eventReadDtos) {
        return new CompilationDto(eventReadDtos,
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle());
    }
}
