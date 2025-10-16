package ru.yandex.practicum.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.request.ParticipationRequestDto;
import ru.yandex.practicum.request.service.RequestService;
import java.util.List;

@RequestMapping("/users")
@RestController
@AllArgsConstructor
public class PrivateRequestController {

    private final RequestService requestService;

    @PostMapping("{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable long userId, @RequestParam int eventId) {
       return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable long userId,
                                                 @PathVariable long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable long userId) {
        return requestService.getUserRequests(userId);
    }
}
