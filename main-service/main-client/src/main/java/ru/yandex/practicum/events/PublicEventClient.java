package ru.yandex.practicum.events;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import ru.yandex.practicum.CreateEndpointHitDto;
import ru.yandex.practicum.event.EventFullDto;
import ru.yandex.practicum.event.EventSearchParams;
import ru.yandex.practicum.exception.StatsClient;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicEventClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final StatsClient statsClient;

    public PublicEventClient(@Value("${main-service.url}") String mainServiceUrl,
                             RestTemplateBuilder builder, StatsClient statsClient) {
        this.baseUrl = mainServiceUrl;
        this.restTemplate = builder.build();
        this.statsClient = statsClient;
    }

    public List<EventFullDto> getAll(EventSearchParams params, String ip, String uri) {
        CreateEndpointHitDto hit = new CreateEndpointHitDto();
        hit.setApp("main-service");
        hit.setUri(uri);
        hit.setIp(ip);
        statsClient.createHit(hit);
        try {
            StringBuilder urlBuilder = new StringBuilder(baseUrl + "/events?from=" + params.getFrom() + "&size=" + params.getSize());
            if (params.getText() != null) urlBuilder.append("&text=").append(params.getText());
            if (params.getCategories() != null && !params.getCategories().isEmpty()) {
                for (Long catId : params.getCategories()) {
                    urlBuilder.append("&categories=").append(catId);
                }
            }
            if (params.getPaid() != null) urlBuilder.append("&paid=").append(params.getPaid());
            if (params.getRangeStart() != null) urlBuilder.append("&rangeStart=").append(params.getRangeStart());
            if (params.getRangeEnd() != null) urlBuilder.append("&rangeEnd=").append(params.getRangeEnd());
            urlBuilder.append("&onlyAvailable=").append(params.isOnlyAvailable());
            if (params.getSort() != null) urlBuilder.append("&sort=").append(params.getSort());

            EventFullDto[] response = restTemplate.getForObject(urlBuilder.toString(), EventFullDto[].class);
            return response != null ? Arrays.asList(response) : List.of();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении событий: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public EventFullDto getById(int id, String ip, String uri) {
        CreateEndpointHitDto hit = new CreateEndpointHitDto();
        hit.setApp("main-service");
        hit.setUri(uri);
        hit.setIp(ip);
        statsClient.createHit(hit);
        try {
            return restTemplate.getForObject(baseUrl + "/events/{id}", EventFullDto.class, id);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении события: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }
}