package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.*;
import ru.yandex.practicum.event.model.AdminEventSearchParams;
import ru.yandex.practicum.event.model.EventSearchParams;
import ru.yandex.practicum.request.ParticipationRequestDto;
import java.util.List;

public interface EventService {
    void update(int eventId, UpdateEventAdminRequest eventAdminRequest);
    List<EventFullDto> getAll(AdminEventSearchParams eventCreate);
    List<EventFullDto> findAll(EventSearchParams eventSearchParams);
    EventFullDto getById(int id);
    EventFullDto create(Long userId, NewEventDto newEventDto);
    void updatePrivateUser(long userId, NewEventDto newEventDto, int eventId);
    List<EventFullDto> findByUserId(long userId, int from, int size);
    EventFullDto getEventByUser(long userId, long eventId);
    List<ParticipationRequestDto> getEvent(long userId, long eventId);
    EventRequestStatusResult updateStatus(long userId, int eventId, EventRequestStatusUpdateRequest updateRequest);
}
