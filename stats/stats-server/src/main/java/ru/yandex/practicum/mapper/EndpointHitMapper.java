package ru.yandex.practicum.mapper;

import ru.yandex.practicum.CreateEndpointHitDto;
import ru.yandex.practicum.StatsRequest;
import ru.yandex.practicum.module.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EndpointHitMapper {
    public static EndpointHit mapToEndpointHit(CreateEndpointHitDto createEndpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(createEndpointHitDto.getApp());
        endpointHit.setUri(createEndpointHitDto.getUri());
        endpointHit.setIp(createEndpointHitDto.getIp());
        endpointHit.setCreated(LocalDateTime.now());
        return endpointHit;
    }

    public static StatsRequest mapToStatsRequest(String start, String end, List<String> uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);
        return new StatsRequest(startDate, endDate, uris, unique);
    }
}


