package ru.yandex.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ViewStatsDto;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.event.dto.*;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.StatsClient;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServicelmpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient client;

    @Override
    @Transactional
    public EventFullDto saveEvent(NewEventDto eventWriteDto, Integer userId) {
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Нет такого пользователя !"));
        Category category = categoryRepository.findById(eventWriteDto.getCategory()).orElseThrow(() ->
                new NotFoundException("Нет такой категории !"));
        if (eventWriteDto.getEventDate().isBefore(LocalDateTime.now().plusMinutes(120))) {
            throw new ValidationException("Событие проводится менее, чем через два часа от текущего момента !",
                    HttpStatus.BAD_REQUEST);
        }
        Event event = EventMapper.saveEventWriteDtoToEvent(eventWriteDto, category, initiator);
        eventRepository.save(event);
        return EventMapper.eventToFullDto(event, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByIdByOwner(Integer eventId, Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Нет такого пользователя");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Нет такого события !"));
        if (event.getPublishedOn() == null) {
            return EventMapper.eventToFullDto(event, 0L);
        } else {
            List<ViewStatsDto> hitReadDto = client.getStats(event.getPublishedOn(),
                    LocalDateTime.now(),
                    List.of("/events/" + eventId),
                    true);
            if (hitReadDto.isEmpty()) {
                return EventMapper.eventToFullDto(event, 0L);
            } else {
                return EventMapper.eventToFullDto(event, hitReadDto.getFirst().getHits());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getListByOwner(Integer userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Нет такого пользователя");
        }
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        List<Event> eventsResult = eventRepository.findByInitiatorId(userId, pageable).getContent();
        if (eventsResult.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Integer, String> urisById = eventsResult.stream()
                .filter(entity -> entity.getPublishedOn() != null)
                .collect(Collectors.toMap(entity -> entity.getId(), entity -> "/events/" + entity.getId()));
        if (urisById.isEmpty()) {
            return eventsResult.stream()
                    .map(event -> EventMapper.eventToShortDto(event, 0))
                    .toList();
        }
        LocalDateTime minStart = eventsResult.stream()
                .filter(entity -> entity.getPublishedOn() != null)
                .map(entity -> entity.getPublishedOn())
                .min(LocalDateTime::compareTo).get();
        Map<Integer, Integer> viewsById = Collections.emptyMap();
        List<ViewStatsDto> stats = client.getStats(minStart,
                LocalDateTime.now(),
                new ArrayList<>(urisById.values()),
                true);
        viewsById = stats.stream().collect(Collectors.toMap(
                s -> Integer.parseInt(s.getUri().substring(s.getUri().lastIndexOf('/') + 1)),
                s -> s.getHits().intValue()));
        final Map<Integer, Integer> viewsByIdFinal = viewsById;
        return eventsResult.stream()
                .map(entity -> {
                    Integer views = 0;
                    if (entity.getPublishedOn() != null) {
                        views = viewsByIdFinal.getOrDefault(entity.getId(), 0);
                    }
                    return EventMapper.eventToShortDto(entity, views);
                })
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(NewEventDto eventWriteDto, Integer eventId, Integer userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Нет такого события !"));
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Нет такого пользователя !");
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Пользователь редактирует не своё событие !", HttpStatus.CONFLICT);
        }
        if (event.getState() == State.PUBLISHED) {
            throw new ValidationException("Изменить можно только отмененные события, либо события, ожидающие модерацию !",
                    HttpStatus.CONFLICT);
        }
        if (eventWriteDto.getEventDate() != null && eventWriteDto.getEventDate().isBefore(LocalDateTime.now()) &&
                eventWriteDto.getEventDate().isBefore(LocalDateTime.now().plusMinutes(120))) {
            throw new ValidationException("Событие проводится менее, чем через два часа от текущего момента !",
                    HttpStatus.CONFLICT);
        }
        if (eventWriteDto.getAnnotation() != null) {
            event.setAnnotation(eventWriteDto.getAnnotation());
        }
        if (eventWriteDto.getDescription() != null) {
            event.setDescription(eventWriteDto.getDescription());
        }
        if (eventWriteDto.getEventDate() != null) {
            event.setEventDate(eventWriteDto.getEventDate());
        }
        if (eventWriteDto.getLocation() != null) {
            event.setLocation(eventWriteDto.getLocation());
        }
        if (eventWriteDto.getPaid() != null) {
            event.setPaid(eventWriteDto.getPaid());
        }
        if (eventWriteDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventWriteDto.getParticipantLimit());
        }
        if (eventWriteDto.getRequestModeration() != null) {
            event.setRequestModeration(eventWriteDto.getRequestModeration());
        }
        if (eventWriteDto.getTitle() != null) {
            event.setTitle(eventWriteDto.getTitle());
        }
        if (eventWriteDto.getStateAction() != null &&
                (event.getState() == State.PENDING || event.getState() == State.CANCELED)) {
            if (eventWriteDto.getStateAction() == Action.SEND_TO_REVIEW) {
                event.setState(State.PENDING);
            } else if (eventWriteDto.getStateAction() == Action.CANCEL_REVIEW) {
                event.setState(State.CANCELED);
            }
        }
        return EventMapper.eventToFullDto(event, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getListByAdmin(List<Integer> users,
                                                 List<State> states,
                                                 List<Integer> categories,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Integer from,
                                                 Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        List<Event> eventsResult;
        if (rangeStart == null || rangeEnd == null) {
            eventsResult = eventRepository.getListByAdminWithoutDates(users,
                    states,
                    categories,
                    pageable).getContent();
        } else {
            eventsResult = eventRepository.getListByAdminWithDates(users,
                    states,
                    categories,
                    rangeStart,
                    rangeEnd,
                    pageable).getContent();
        }
        if (eventsResult.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Integer, String> urisById = eventsResult.stream()
                .filter(entity -> entity.getPublishedOn() != null)
                .collect(Collectors.toMap(entity -> entity.getId(), entity -> "/events/" + entity.getId()));
        if (urisById.isEmpty()) {
            return eventsResult.stream()
                    .map(event -> EventMapper.eventToFullDto(event, 0L))
                    .toList();
        }
        LocalDateTime minStart = eventsResult.stream()
                .filter(entity -> entity.getPublishedOn() != null)
                .map(entity -> entity.getPublishedOn())
                .min(LocalDateTime::compareTo).get();

        Map<Integer, Integer> viewsById = Collections.emptyMap();

        List<ViewStatsDto> stats = client.getStats(minStart,
                LocalDateTime.now(),
                new ArrayList<>(urisById.values()),
                true);
        viewsById = stats.stream().collect(Collectors.toMap(
                s -> Integer.parseInt(s.getUri().substring(s.getUri().lastIndexOf('/') + 1)),
                s -> s.getHits().intValue()));
        final Map<Integer, Integer> viewsByIdFinal = viewsById;
        return eventsResult.stream()
                .map(entity -> {
                    Integer views = 0;
                    if (entity.getPublishedOn() != null) {
                        views = viewsByIdFinal.getOrDefault(entity.getId(), 0);
                    }
                    return EventMapper.eventToFullDto(entity, Long.valueOf(views));
                })
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(NewEventDto eventWriteDto, Integer eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Нет такого события !"));
        if (event.getState() != State.PENDING) {
            throw new ValidationException("Редактировать можно только событие в состоянии ожидания публикации",
                    HttpStatus.CONFLICT);
        }
        if (eventWriteDto.getAnnotation() != null) {
            event.setAnnotation(eventWriteDto.getAnnotation());
        }
        if (eventWriteDto.getDescription() != null) {
            event.setDescription(eventWriteDto.getDescription());
        }
        if (eventWriteDto.getEventDate() != null) {
            event.setEventDate(eventWriteDto.getEventDate());
        }
        if (eventWriteDto.getLocation() != null) {
            event.setLocation(eventWriteDto.getLocation());
        }
        if (eventWriteDto.getPaid() != null) {
            event.setPaid(eventWriteDto.getPaid());
        }
        if (eventWriteDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventWriteDto.getParticipantLimit());
        }
        if (eventWriteDto.getRequestModeration() != null) {
            event.setRequestModeration(eventWriteDto.getRequestModeration());
        }
        if (eventWriteDto.getTitle() != null) {
            event.setTitle(eventWriteDto.getTitle());
        }
        if (eventWriteDto.getStateAction() != null) {
            if (eventWriteDto.getStateAction() == Action.PUBLISH_EVENT) {

                if (event.getEventDate().isBefore((LocalDateTime.now().plusMinutes(60)))) {
                    throw new ValidationException("Дата события должна быть не ранее, чем через час от даты публикации !",
                            HttpStatus.CONFLICT);
                }
                if (event.getState() != State.PENDING) {
                    throw new ValidationException("Редактировать можно только событие в состоянии ожидания публикации",
                            HttpStatus.CONFLICT);
                }
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (eventWriteDto.getStateAction() == Action.REJECT_EVENT) {
                if (event.getState() == State.PUBLISHED) {
                    throw new ValidationException("Редактировать можно только событие в состоянии ожидания публикации",
                            HttpStatus.CONFLICT);
                }
                event.setState(State.CANCELED);
            }
        }
        if (event.getPublishedOn() != null && event.getEventDate().isBefore(event.getPublishedOn().plusMinutes(60))) {
            throw new ValidationException("Дата события должна быть не ранее, чем через час от даты публикации !",
                    HttpStatus.CONFLICT);
        }
        return EventMapper.eventToFullDto(event, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByPublic(String text,
                                                     List<Integer> categories,
                                                     Boolean paid,
                                                     LocalDateTime rangeStart,
                                                     LocalDateTime rangeEnd,
                                                     boolean onlyAvailable,
                                                     String sort,
                                                     Integer from,
                                                     Integer size) {
        String lowerText = "";
        if (text != null && !text.isBlank()) {
            lowerText = "%" + text.toLowerCase() + "%";
        }
        if ((rangeStart != null) && (rangeEnd != null) && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Некорректные даты !", HttpStatus.BAD_REQUEST);
        }
        int page = from / size;
        Sort sortObj = switch (sort) {
            case "EVENT_DATE" -> Sort.by(Sort.Direction.DESC, "eventDate");
            default           -> Sort.by(Sort.Direction.DESC, "id");
        };
        Pageable pageable = PageRequest.of(page, size, sortObj);
        List<Event> eventsResult;
        State publishedState = State.PUBLISHED;
        if (rangeEnd == null || rangeStart == null) {
            eventsResult = eventRepository.getEventsByPublicWithoutDates(publishedState, lowerText,
                    categories,
                    paid,
                    onlyAvailable,
                    pageable);
        } else {
            eventsResult = eventRepository.getEventsByPublicWithDates(publishedState, lowerText,
                    categories,
                    paid,
                    rangeStart,
                    rangeEnd,
                    onlyAvailable,
                    pageable).getContent();
        }
        if (eventsResult.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Integer, String> urisById = eventsResult.stream()
                .filter(entity -> entity.getPublishedOn() != null)
                .collect(Collectors.toMap(entity -> entity.getId(), entity -> "/events/" + entity.getId()));
        if (urisById.isEmpty()) {
            return eventsResult.stream()
                    .map(event -> EventMapper.eventToShortDto(event, 0))
                    .toList();
        }
        LocalDateTime minStart = eventsResult.stream()
                .filter(entity -> entity.getPublishedOn() != null)
                .map(entity -> entity.getPublishedOn())
                .min(LocalDateTime::compareTo).get();
        Map<Integer, Integer> viewsById = Collections.emptyMap();
        List<ViewStatsDto> stats = client.getStats(minStart,
                LocalDateTime.now(),
                new ArrayList<>(urisById.values()),
                true);
        viewsById = stats.stream().collect(Collectors.toMap(
                s -> Integer.parseInt(s.getUri().substring(s.getUri().lastIndexOf('/') + 1)),
                s -> s.getHits().intValue()));
        final Map<Integer, Integer> viewsByIdFinal = viewsById;
        List<EventShortDto> returned = eventsResult.stream()
                .map(entity -> {
                    Integer views = 0;
                    if (entity.getPublishedOn() != null) {
                        views = viewsByIdFinal.getOrDefault(entity.getId(), 0);
                    }
                    return EventMapper.eventToShortDto(entity, views);
                })
                .toList();
        if (sort.equals("VIEWS")) {
            return returned.stream()
                    .sorted(Comparator.comparingInt(EventShortDto::getViews).reversed())
                    .toList();
        } else {
            return returned;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdByPublic(Integer eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Нет такого события !"));
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Нет такого события !");
        }
        return EventMapper.eventToFullDto(event, 0L);
    }

    @Override
    public EventFullDto setViewsToEvent(HttpServletRequest request, EventFullDto eventReadFullDto) {
        String[] uri = request.getRequestURI().split("/");
        log.info("uri:" + request.getRequestURI());
        List<ViewStatsDto> hitReadDtoList = client.getStats(LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(5),
                List.of(request.getRequestURI()),
                true);
        ViewStatsDto hitReadDto = hitReadDtoList.getFirst();
        eventReadFullDto.setViews(hitReadDto.getHits());
        return eventReadFullDto;
    }
}