package ru.yandex.practicum.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.compilation.CompilationDto;
import ru.yandex.practicum.compilation.NewCompilationDto;
import ru.yandex.practicum.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.compilation.NewCompilation;
import ru.yandex.practicum.compilation.repository.CompilationRepository;
import ru.yandex.practicum.event.EventShortDto;
import ru.yandex.practicum.event.exception.ConflictException;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class CompilationServicelmpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private  final CompilationMapper compilationMapper;

    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        if (events.size() != newCompilationDto.getEvents().size()) {
            throw new NotFoundException("Некоторые события не найдены");
        }
        if (compilationRepository.existsByTitle(newCompilationDto.getTitle())) {
            throw new ConflictException("Подборка с таким названием уже существует");
        }
        Compilation compilation = CompilationMapper.mapToCompilation(newCompilationDto, events);
        Compilation saved = compilationRepository.save(compilation);
        return compilationMapper.toDto(saved);
    }

    @Transactional
    @Override
    public CompilationDto update(Long compId, NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена или недоступна"));
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.isPinned());
        compilation.setEvents(events);
        Compilation saved = compilationRepository.save(compilation);
        return compilationMapper.toDto(saved);
    }

    @Transactional
    @Override
    public void delete(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Подборка не найдена или недоступна");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(NewCompilation newCompilation) {
        Pageable pageable = PageRequest.of(newCompilation.getFrom() / newCompilation.getSize(),
                newCompilation.getSize());

        Page<Compilation> compilations;
        if (newCompilation.getPinned() == null) {
            compilations = compilationRepository.getAll(pageable);
        } else {
            compilations = compilationRepository.getAllByPinned(newCompilation.getPinned(), pageable);
        }
        if (compilations.isEmpty()) {
            return Collections.emptyList();
        }
        List<CompilationDto> dtos = compilations.getContent().stream()
                .map(compilation -> {
                    CompilationDto dto = compilationMapper.toDto(compilation);
                    List<EventShortDto> events = eventRepository.findAllByCompilation_Id(compilation.getId())
                            .stream()
                            .map(eventMapper::toShortDto)
                            .toList();

                    dto.setEvents(events);
                    return dto;
                })
                .toList();
        return dtos;
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Подборка не найдена или недоступна"));
        CompilationDto compilationDto = compilationMapper.toDto(compilation);
        return compilationDto;
    }
}
