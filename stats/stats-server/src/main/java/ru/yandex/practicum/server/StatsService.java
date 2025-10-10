package ru.yandex.practicum.server;

import ru.yandex.practicum.CreateEndpointHitDto;
import ru.yandex.practicum.StatsRequest;
import ru.yandex.practicum.ViewStatsDto;

import java.util.List;

public interface StatsService {
    void create(CreateEndpointHitDto createEndpointHitDto);

    List<ViewStatsDto> getStats(StatsRequest statsRequest);
}
