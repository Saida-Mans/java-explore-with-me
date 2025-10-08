package ru.yandex.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.CreateEndpointHitDto;
import ru.yandex.practicum.StatsRequest;
import ru.yandex.practicum.ViewStatsDto;
import ru.yandex.practicum.mapper.EndpointHitMapper;
import ru.yandex.practicum.server.StatsService;

import java.util.List;

@RestController
@AllArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody CreateEndpointHitDto createEndpointHitDto) {
         statsService.create(createEndpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam("start") String start, @RequestParam("end") String end,
                                       @RequestParam(value = "uris", required = false) List<String> uris,
                                       @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        StatsRequest statsRequest = EndpointHitMapper.mapToStatsRequest(start, end, uris, unique);
        return statsService.getStats(statsRequest);
    }
}
