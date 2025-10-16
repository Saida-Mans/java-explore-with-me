package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.EventFullDto;
import ru.yandex.practicum.event.EventRequestStatusResult;
import ru.yandex.practicum.event.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.event.NewEventDto;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.request.ParticipationRequestDto;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class PrivateEventController {

    private final EventService eventService;

    @PostMapping("{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId,
                               @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.create(userId, newEventDto);
    }

    @PatchMapping("{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long userId,@RequestBody @Valid NewEventDto newEventDto, @PathVariable int eventId) {
        eventService.updatePrivateUser(userId, newEventDto, eventId);
    }

    @GetMapping("{userId}/events")
    public List<EventFullDto> findByUserId(@PathVariable long userId, @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size) {
        return eventService.findByUserId(userId, from, size);
    }

    @GetMapping("{userId}/events/{eventId}")
    public EventFullDto getEventByUser(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getEventByUser(userId, eventId);
    }

    @GetMapping("{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public EventRequestStatusResult updateStatus(@PathVariable long userId, @RequestBody @Valid EventRequestStatusUpdateRequest newEventDto, @PathVariable int eventId) {
       return eventService.updateStatus(userId, eventId, newEventDto);
    }
}
