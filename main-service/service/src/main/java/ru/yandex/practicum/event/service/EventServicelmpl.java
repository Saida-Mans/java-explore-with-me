package ru.yandex.practicum.event.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.event.*;
import ru.yandex.practicum.event.exception.ConflictException;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.AdminEventSearchParams;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.EventSearchParams;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.ParticipationRequestDto;
import ru.yandex.practicum.request.mapper.RequestMapper;
import ru.yandex.practicum.request.model.Requests;
import ru.yandex.practicum.request.repository.RequestRepository;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServicelmpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private  final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public void update(int eventId, UpdateEventAdminRequest eventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));
        LocalDateTime now = LocalDateTime.now();
        if (eventAdminRequest.getEventDate().isBefore(now.plusHours(1))) {
               throw new ConflictException("Дата начала события должна быть не ранее чем за час от даты публикации");
           }
        if (eventAdminRequest.getAction() == Action.PUBLISH_EVENT) {
            if (event.getState() != State.PENDING) {
                throw new ConflictException("Событие нельзя опубликовать, оно не в состоянии ожидания публикации (PENDING)");
            }
            event.setState(State.PUBLISHED);
            event.setPublishedOn(now);
        }
        if (eventAdminRequest.getAction() == Action.REJECT_EVENT) {
            if (event.getState() == State.PUBLISHED) {
                throw new ConflictException("Событие нельзя отклонить, оно уже опубликовано");
            }
            event.setState(State.CANCELED);
        }
        if (eventAdminRequest.getAnnotation() != null) event.setAnnotation(eventAdminRequest.getAnnotation());
        if (eventAdminRequest.getCategory() != 0) event.setCategory(
                categoryRepository.findById(eventAdminRequest.getCategory())
                        .orElseThrow(() -> new NotFoundException("Категория не найдена"))
        );
        if (eventAdminRequest.getDescription() != null) event.setDescription(eventAdminRequest.getDescription());
        if (eventAdminRequest.getEventDate() != null) event.setEventDate(eventAdminRequest.getEventDate());
        if (eventAdminRequest.getLocation() != null) {
            event.setLat(eventAdminRequest.getLocation().getLat());
            event.setLon(eventAdminRequest.getLocation().getLon());
        }
        event.setPaid(eventAdminRequest.isPaid());
        event.setParticipantLimit(eventAdminRequest.getParticipantLimit());
        event.setRequestModeration(eventAdminRequest.isRequestModeration());
        if (eventAdminRequest.getTitle() != null) event.setTitle(eventAdminRequest.getTitle());
    }

    @Override
    public List<EventFullDto> findAll(EventSearchParams params) {
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Page<Event> page = eventRepository.findAll(
                params.getText(),
                params.getCategories(),
                params.getPaid(),
                params.getRangeStart(),
                params.getRangeEnd(),
                params.isOnlyAvailable(),
                pageable);
        List<Event> events = page.getContent();
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        if ("VIEWS".equalsIgnoreCase(params.getSort())) {
            events.sort(Comparator.comparingInt(Event::getViews).reversed());
        } else if ("EVENT_DATE".equalsIgnoreCase(params.getSort())) {
            events.sort(Comparator.comparing(Event::getEventDate));
        }
        return events.stream()
                .map(EventMapper::mapToEventFullDto)
                .toList();
    }

    @Override
    public List<EventFullDto> getAll(AdminEventSearchParams params) {
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Page<Event> page = eventRepository.findAdminEvents(
                params.getUsers(),
                params.getStates().stream().map(Enum::name).toList(),
                params.getCategories(),
                params.getRangeStart(),
                params.getRangeEnd(),
                pageable
        );
        if (page.isEmpty()) {
            return Collections.emptyList();
        }
        return page.getContent().stream()
                .map(EventMapper::mapToEventFullDto)
                .toList();
    }

   public EventFullDto getById(int id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));
       if (event.getState() != State.PUBLISHED) {
           throw new NotFoundException("Событие не найдено или недоступно");
       }
        EventFullDto eventFullDto = eventMapper.mapToEventFullDto(event);
        return eventFullDto;
    }

    @Transactional
    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        LocalDateTime now = LocalDateTime.now();
        if (newEventDto.getEventDate().isBefore(now.plusHours(2))) {
            throw new ConflictException("Дата и время события должны быть не раньше, чем через 2 часа от текущего момента");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Category category = categoryRepository.findById(newEventDto.getCategoryDto().getId())
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        Event event = eventMapper.toEvent(newEventDto, category, user);
        event.setInitiator(user);
        event.setState(State.PENDING);
        eventRepository.save(event);
        return eventMapper.mapToEventFullDto(event);
    }

    @Transactional
    @Override
    public void updatePrivateUser(long userId, NewEventDto newEventDto, int eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Собцытие не найдено"));
        if (event.getState() != State.PENDING && event.getState() != State.CANCELED) {
            throw new ConflictException("Событие должно быть в статусе ожидания или отменены");
        }
        LocalDateTime now = LocalDateTime.now();
        if (event.getEventDate().isBefore(now.plusHours(2))) {
            throw new ConflictException("Дата и время события должны быть не раньше, чем через 2 часа от текущего момента");
        }
        Category category = categoryRepository.findById(newEventDto.getCategoryDto().getId())
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        eventMapper.updateEventFromDto(newEventDto, event, category);
        eventRepository.save(event);
    }

    @Override
    public List<EventFullDto> findByUserId(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> events = eventRepository.findByInitiatorId(userId, pageable);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        return events.stream()
                .map(EventMapper::mapToEventFullDto)
                .toList();
    }

    @Override
    public EventFullDto getEventByUser(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getEvent(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Requests> requests = requestRepository.getRequests(userId, eventId);
        List<ParticipationRequestDto> dto = requests.stream()
                .map(RequestMapper::toDto)
                .toList();
      return dto;
    }

    @Transactional
    @Override
    public EventRequestStatusResult updateStatus(long userId, int eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("Событие недоступно для данного пользователя");
        }
        List<Requests> requests = requestRepository.findAllById(updateRequest.getRequestIds());
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        int confirmedCount = (int) event.getRequests().stream()
                .filter(r -> r.getStatus() == Status.CONFIRMED)
                .count();
        for (Requests req : requests) {
            if (req.getStatus() != Status.PENDING) {
                throw new ConflictException("Запрос в статусе ожидания должен быть");
            }
            if (updateRequest.getStatus() == Status.CONFIRMED) {
                if (event.getParticipantLimit() > 0 && confirmedCount >= event.getParticipantLimit()) {
                    req.setStatus(Status.REJECTED);
                    rejected.add(RequestMapper.toDto(req));
                } else {
                    req.setStatus(Status.CONFIRMED);
                    confirmedCount++;
                    confirmed.add(RequestMapper.toDto(req));
                }
            } else if (updateRequest.getStatus() == Status.REJECTED) {
                req.setStatus(Status.REJECTED);
                rejected.add(RequestMapper.toDto(req));
            }
        }
        requestRepository.saveAll(requests);
        EventRequestStatusResult result = new EventRequestStatusResult();
        result.setConfirmedRequests(confirmed);
        result.setRejectedRequests(rejected);
        return result;
    }
}