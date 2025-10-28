package ru.yandex.practicum.server;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.CreateEndpointHitDto;
import ru.yandex.practicum.StatsRequest;
import ru.yandex.practicum.ViewStatsDto;
import ru.yandex.practicum.mapper.EndpointHitMapper;
import ru.yandex.practicum.module.EndpointHit;
import ru.yandex.practicum.repository.StatsRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class StatsServicelmpl implements StatsService {

    private final StatsRepository statsRepository;

    @Transactional
    @Override
    public void create(CreateEndpointHitDto createEndpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.mapToEndpointHit(createEndpointHitDto);
        statsRepository.save(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(StatsRequest statsRequest) {
        List<String> uris = statsRequest.getUris();
        boolean hasUris = uris != null && !uris.isEmpty();

        if (statsRequest.isUnique()) {
            return hasUris
                    ? statsRepository.getUniqueIpStats(statsRequest.getStart(), statsRequest.getEnd(), uris)
                    : statsRepository.getUniqueIpStatsWithoutUris(statsRequest.getStart(), statsRequest.getEnd());
        } else {
            return hasUris
                    ? statsRepository.getAllIpStats(statsRequest.getStart(), statsRequest.getEnd(), uris)
                    : statsRepository.getAllIpStatsWithoutUris(statsRequest.getStart(), statsRequest.getEnd());
        }
    }
}

