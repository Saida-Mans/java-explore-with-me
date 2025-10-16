package ru.yandex.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.CreateEndpointHitDto;
import ru.yandex.practicum.event.EventFullDto;
import ru.yandex.practicum.event.EventSearchParams;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.exception.StatsClient;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    public List<EventFullDto> getAll(@RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(defaultValue = "false") Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        CreateEndpointHitDto hit = new CreateEndpointHitDto();
        hit.setApp("main-service");
        hit.setUri(request.getRequestURI());
        hit.setIp(request.getRemoteAddr());
        statsClient.createHit(hit);
        EventSearchParams eventSearchParams = new EventSearchParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.findAll(eventSearchParams);
    }

    @GetMapping("{id}")
    public EventFullDto getById(@PathVariable int id, HttpServletRequest request) {
        CreateEndpointHitDto hit = new CreateEndpointHitDto();
        hit.setApp("main-service");
        hit.setUri(request.getRequestURI());
        hit.setIp(request.getRemoteAddr());
        statsClient.createHit(hit);
        return eventService.getById(id);
    }
}
