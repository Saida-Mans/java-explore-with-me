package ru.yandex.practicum.compilation.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ViewStatsDto;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.compilation.repository.CompilationRepository;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.StatsClient;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatsClient client;

    @Override
    public CompilationDto saveCompilation(NewCompilationDto compilationWriteDto) {
        Set<Event> events;
        if (compilationWriteDto.getEvents() != null && !compilationWriteDto.getEvents().isEmpty()) {
            Set<Integer> eventsIds = compilationWriteDto.getEvents();
            events = new HashSet<>(eventRepository.findAllById(eventsIds));
        } else {
            events = Collections.emptySet();
        }
        Compilation compilation = new Compilation(events);
        compilation.setTitle(compilationWriteDto.getTitle());
        if (compilationWriteDto.getPinned() == null) {
            compilation.setPinned(false);
        } else {
            compilation.setPinned(compilationWriteDto.getPinned());
        }
        compilationRepository.save(compilation);
        if (!events.isEmpty()) {
            Map<Integer, String> urisById = events.stream()
                    .filter(entity -> entity.getPublishedOn() != null)
                    .collect(Collectors.toMap(entity -> entity.getId(), entity -> "/events/" + entity.getId()));
            if (urisById.isEmpty()) {
                List<EventShortDto> eventReadShortDtos = events.stream()
                        .map(event -> EventMapper.eventToShortDto(event, 0))
                        .toList();
                return CompilationMapper.compilationToReadDto(compilation, eventReadShortDtos);
            }
            LocalDateTime minStart = events.stream()
                    .filter(entity -> entity.getPublishedOn() != null)
                    .map(entity -> entity.getPublishedOn())
                    .min(LocalDateTime::compareTo).get();
            List<ViewStatsDto> stats = client.getStats(minStart,
                    LocalDateTime.now(),
                    new ArrayList<>(urisById.values()),
                    true);
            Map<Integer, Long> viewsById = stats.stream().collect(Collectors.toMap(
                    s -> Integer.parseInt(s.getUri().substring(s.getUri().lastIndexOf('/') + 1)),
                    s -> s.getHits()));
            final Map<Integer, Long> viewsByIdFinal = viewsById;
            List<EventShortDto> resultEvents = events.stream()
                    .map(entity -> {
                        Integer views = 0;
                        if (entity.getPublishedOn() != null) {
                            views = viewsByIdFinal.getOrDefault(entity.getId(), 0L).intValue();;
                        }
                        return EventMapper.eventToShortDto(entity, views);
                    })
                    .toList();
            return CompilationMapper.compilationToReadDto(compilation, resultEvents);
        } else {
            return CompilationMapper.compilationToReadDto(compilation, Collections.emptyList());
        }
    }

    @Override
    public void deleteCompilation(Integer compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Нет такой подборки !");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(NewCompilationDto compilationWriteDto, Integer compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Нет такой подборки !"));
        if (compilationWriteDto.getPinned() != null) {
            compilation.setPinned(compilationWriteDto.getPinned());
        }
        if (compilationWriteDto.getTitle() != null) {
            compilation.setTitle(compilationWriteDto.getTitle());
        }
        Set<Event> newEvents;
        if (compilationWriteDto.getEvents() != null && !compilationWriteDto.getEvents().isEmpty()) {
            Set<Integer> eventsIds = compilationWriteDto.getEvents();
            newEvents = new HashSet<>(eventRepository.findAllById(eventsIds));
        } else {
            newEvents = Collections.emptySet();
        }
        compilation.getEvents().clear();
        compilation.getEvents().addAll(newEvents);
        if (!newEvents.isEmpty()) {
            Map<Integer, String> urisById = newEvents.stream()
                    .filter(entity -> entity.getPublishedOn() != null)
                    .collect(Collectors.toMap(entity -> entity.getId(), entity -> "/events/" + entity.getId()));
            if (urisById.isEmpty()) {
                List<EventShortDto> eventReadShortDtos = newEvents.stream()
                        .map(event -> EventMapper.eventToShortDto(event, 0))
                        .toList();
                return CompilationMapper.compilationToReadDto(compilation, eventReadShortDtos);
            }
            LocalDateTime minStart = newEvents.stream()
                    .filter(entity -> entity.getPublishedOn() != null)
                    .map(entity -> entity.getPublishedOn())
                    .min(LocalDateTime::compareTo).get();
            List<ViewStatsDto> stats = client.getStats(minStart,
                    LocalDateTime.now(),
                    new ArrayList<>(urisById.values()),
                    true);
            Map<Integer, Long> viewsById = stats.stream().collect(Collectors.toMap(
                    s -> Integer.parseInt(s.getUri().substring(s.getUri().lastIndexOf('/') + 1)),
                    s -> s.getHits()));
            final Map<Integer, Long> viewsByIdFinal = viewsById;
            List<EventShortDto> resultEvents = newEvents.stream()
                    .map(entity -> {
                        Integer views = 0;
                        if (entity.getPublishedOn() != null) {
                            views = viewsByIdFinal.getOrDefault(entity.getId(), 0L).intValue();;
                        }
                        return EventMapper.eventToShortDto(entity, views);
                    })
                    .toList();
            return CompilationMapper.compilationToReadDto(compilation, resultEvents);
        } else {
            return CompilationMapper.compilationToReadDto(compilation, Collections.emptyList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilationListByPublic(Boolean pinned, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        List<Compilation> compilations = compilationRepository.getCompilationListByPublic(pinned, pageable).getContent();
        return compilations.stream()
                .map(compilation -> {
                    Set<Event> events;
                    if (!compilation.getEvents().isEmpty()) {
                        events = compilation.getEvents();
                    } else {
                        events = Collections.emptySet();
                    }
                    if (!events.isEmpty()) {
                        Map<Integer, String> urisById = events.stream()
                                .filter(entity -> entity.getPublishedOn() != null)
                                .collect(Collectors.toMap(entity -> entity.getId(), entity -> "/events/" + entity.getId()));

                        if (urisById.isEmpty()) {
                            List<EventShortDto> eventReadShortDtos = events.stream()
                                    .map(event -> EventMapper.eventToShortDto(event, 0))
                                    .toList();
                            return CompilationMapper.compilationToReadDto(compilation, eventReadShortDtos);
                        }
                        LocalDateTime minStart = events.stream()
                                .filter(entity -> entity.getPublishedOn() != null)
                                .map(entity -> entity.getPublishedOn())
                                .min(LocalDateTime::compareTo).get();
                        Map<Integer, Long> viewsById = Collections.emptyMap();
                        List<ViewStatsDto> stats = client.getStats(minStart,
                                LocalDateTime.now(),
                                new ArrayList<>(urisById.values()),
                                true);
                        viewsById = stats.stream().collect(Collectors.toMap(
                                s -> Integer.parseInt(s.getUri().substring(s.getUri().lastIndexOf('/') + 1)),
                                s -> s.getHits()));
                        final Map<Integer, Long> viewsByIdFinal = viewsById;
                        List<EventShortDto> resultEvents = events.stream()
                                .map(entity -> {
                                    Integer views = 0;
                                    if (entity.getPublishedOn() != null) {
                                        views = viewsByIdFinal.getOrDefault(entity.getId(), 0L).intValue();
                                    }
                                    return EventMapper.eventToShortDto(entity, views);
                                })
                                .toList();
                        return CompilationMapper.compilationToReadDto(compilation, resultEvents);
                    } else {
                        return CompilationMapper.compilationToReadDto(compilation, Collections.emptyList());
                    }
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationByIdByPublic(Integer compId) {
        Compilation compilation = compilationRepository.findByIdWithGraph(compId).orElseThrow(() ->
                new NotFoundException("Нет такой подборки"));
        Set<Event> compilationEventsSet = compilation.getEvents();
        List<Event> compilationEvents = compilationEventsSet.stream().toList();
        if (!compilationEvents.isEmpty()) {
            Map<Integer, String> urisById = compilationEvents.stream()
                    .filter(entity -> entity.getPublishedOn() != null)
                    .collect(Collectors.toMap(entity -> entity.getId(), entity -> "/events/" + entity.getId()));
            if (urisById.isEmpty()) {
                List<EventShortDto> eventReadShortDtos = compilationEvents.stream()
                        .map(event -> EventMapper.eventToShortDto(event, 0))
                        .toList();
                return CompilationMapper.compilationToReadDto(compilation, eventReadShortDtos);
            }
            LocalDateTime minStart = compilationEvents.stream()
                    .filter(entity -> entity.getPublishedOn() != null)
                    .map(entity -> entity.getPublishedOn())
                    .min(LocalDateTime::compareTo).get();
            Map<Integer, Long> viewsById = Collections.emptyMap();
            List<ViewStatsDto> stats = client.getStats(minStart,
                    LocalDateTime.now(),
                    new ArrayList<>(urisById.values()),
                    true);
            viewsById = stats.stream().collect(Collectors.toMap(
                    s -> Integer.parseInt(s.getUri().substring(s.getUri().lastIndexOf('/') + 1)),
                    s -> s.getHits()));
            final Map<Integer, Long> viewsByIdFinal = viewsById;
            List<EventShortDto> resultEvents = compilationEvents.stream()
                    .map(entity -> {
                        Integer views = 0;
                        if (entity.getPublishedOn() != null) {
                            views = viewsByIdFinal.getOrDefault(entity.getId(), 0L).intValue();;
                        }
                        return EventMapper.eventToShortDto(entity, views);
                    })
                    .toList();
            return CompilationMapper.compilationToReadDto(compilation, resultEvents);
        } else {
            return CompilationMapper.compilationToReadDto(compilation, Collections.emptyList());
        }
    }
}