package ru.yandex.practicum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.request.dto.RequestDto;
import ru.yandex.practicum.request.dto.RequestListDto;
import ru.yandex.practicum.request.dto.UpdateResultDto;
import ru.yandex.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getListByOwner(@PathVariable("userId") Integer userId,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getListByOwner(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@RequestBody @Validated(NewEventDto.OnPost.class) NewEventDto eventWriteDto,
                                      @PathVariable("userId") Integer userId) {
        return eventService.saveEvent(eventWriteDto, userId);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getByIdByOwner(@PathVariable("eventId") Integer eventId,
                                           @PathVariable("userId") Integer userId) {
        return eventService.getByIdByOwner(eventId, userId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody @Validated({NewEventDto.OnUpdateState.class}) NewEventDto eventWriteDto,
                                    @PathVariable("eventId") Integer eventId,
                                    @PathVariable("userId")Integer userId) {
        return eventService.updateEvent(eventWriteDto, eventId, userId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequestsByEventId(@PathVariable("eventId") Integer eventId,
                                                     @PathVariable("userId") Integer userId) {
        return requestService.getRequestsByEventId(eventId, userId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public UpdateResultDto acceptRequestByEventInitiator(@RequestBody @Valid RequestListDto requestListDto,
                                                         @PathVariable("userId") Integer userId,
                                                         @PathVariable("eventId")Integer eventId) {
        return requestService.acceptRequestByEventInitiator(requestListDto, userId, eventId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<RequestDto> getRequestsByUserId(@PathVariable("userId") Integer userId) {
        return requestService.getRequestsByUserId(userId);
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto saveRequest(@PathVariable("userId") Integer userId,
                                  @RequestParam Integer eventId) {
        return requestService.saveRequest(eventId, userId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequestByRequester(@PathVariable("userId") Integer userId,
                                                   @PathVariable("requestId")Integer requestId) {
        return requestService.cancelRequestByRequester(userId, requestId);
    }
}