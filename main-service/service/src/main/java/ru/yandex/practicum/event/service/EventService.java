package ru.yandex.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.event.dto.*;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto saveEvent(NewEventDto eventWriteDto, Integer userId);

    EventFullDto getByIdByOwner(Integer eventId, Integer userId);

    List<EventShortDto> getListByOwner(Integer userId, Integer from, Integer size);

    EventFullDto updateEvent(NewEventDto eventWriteDto, Integer eventId, Integer userId);

    List<EventFullDto> getListByAdmin(List<Integer> users,
                                          List<State> states,
                                          List<Integer> categories,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Integer from,
                                          Integer size);

    EventFullDto updateEventByAdmin(NewEventDto eventWriteDto, Integer eventId);

    List<EventShortDto> getEventsByPublic(String text,
                                              List<Integer> categories,
                                              Boolean paid,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              boolean onlyAvailable,
                                              String sort,
                                              Integer from,
                                              Integer size);

    EventFullDto getEventByIdByPublic(Integer eventId);

    EventFullDto setViewsToEvent(HttpServletRequest request, EventFullDto eventReadFullDto);
}
