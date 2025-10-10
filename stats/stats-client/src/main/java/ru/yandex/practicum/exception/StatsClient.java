package ru.yandex.practicum.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.CreateEndpointHitDto;
import ru.yandex.practicum.StatsRequest;
import ru.yandex.practicum.ViewStatsDto;

import java.util.Arrays;
import java.util.List;

@Service
public class StatsClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public StatsClient(@Value("${stats.url}") String serverUrl, RestTemplateBuilder builder) {
        this.baseUrl = serverUrl;
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build();
    }

    public void createHit(CreateEndpointHitDto createEndpointHitDto) {
        try {
            HttpEntity<CreateEndpointHitDto> request = new HttpEntity<>(createEndpointHitDto);
            restTemplate.exchange(baseUrl + "/hit", HttpMethod.POST, request, Void.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при отправке хита на stats-server: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Stats-server недоступен: " + e.getMessage(), e);
        }
    }

    public List<ViewStatsDto> getStats(StatsRequest statsRequest) {
        try {
            StringBuilder urlBuilder = new StringBuilder(baseUrl + "/stats")
                    .append("?start=").append(statsRequest.getStart())
                    .append("&end=").append(statsRequest.getEnd())
                    .append("&unique=").append(statsRequest.isUnique());

            if (statsRequest.getUris() != null && !statsRequest.getUris().isEmpty()) {
                for (String uri : statsRequest.getUris()) {
                    urlBuilder.append("&uris=").append(uri);
                }
            }
            ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(urlBuilder.toString(), ViewStatsDto[].class);
            return Arrays.asList(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении статистики: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Stats-server недоступен: " + e.getMessage(), e);
        }
    }
}
