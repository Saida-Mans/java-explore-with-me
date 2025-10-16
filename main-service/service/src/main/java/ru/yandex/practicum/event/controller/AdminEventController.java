package ru.yandex.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.EventFullDto;
import ru.yandex.practicum.event.State;
import ru.yandex.practicum.event.UpdateEventAdminRequest;
import ru.yandex.practicum.event.model.AdminEventSearchParams;
import ru.yandex.practicum.event.service.EventService;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@AllArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @PatchMapping("{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int eventId, @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        eventService.update(eventId, updateEventAdminRequest);
    }

    @GetMapping
    public List<EventFullDto> getAll(@RequestParam(required = false) List<Long> users,
                                     @RequestParam(required = false) List<State> states,
                                     @RequestParam(required = false) List<Long> categories,
                                     @RequestParam(required = false) LocalDateTime rangeStart,
                                     @RequestParam(required = false) LocalDateTime rangeEnd,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        AdminEventSearchParams eventcreate = new AdminEventSearchParams(users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getAll(eventcreate);
    }
}
