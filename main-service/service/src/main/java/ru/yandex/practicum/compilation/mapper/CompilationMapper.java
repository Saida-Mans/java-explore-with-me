package ru.yandex.practicum.compilation.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.compilation.CompilationDto;
import ru.yandex.practicum.compilation.NewCompilationDto;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import java.util.List;

@AllArgsConstructor
@Component
public class CompilationMapper {

    private final EventMapper eventMapper;

    public static Compilation mapToCompilation(NewCompilationDto newCompilation, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setPinned(newCompilation.isPinned());
        compilation.setTitle(newCompilation.getTitle());
        compilation.setEvents(events);
        return compilation;
    }

    public CompilationDto toDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setPinned(compilation.isPinned());
        dto.setTitle(compilation.getTitle());
        dto.setEvents(compilation.getEvents().stream()
                .map(eventMapper::toShortDto)
                .toList());
        return dto;
    }
}
