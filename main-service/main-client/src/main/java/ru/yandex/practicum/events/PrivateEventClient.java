package ru.yandex.practicum.events;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import ru.yandex.practicum.event.*;
import ru.yandex.practicum.request.ParticipationRequestDto;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateEventClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PrivateEventClient(@Value("${main-service.url}") String mainServiceUrl, RestTemplateBuilder builder) {
        this.baseUrl = mainServiceUrl;
        this.restTemplate = builder.build();
    }

    public EventFullDto createEvent(long userId, NewEventDto newEventDto) {
        try {
            HttpEntity<NewEventDto> request = new HttpEntity<>(newEventDto);
            ResponseEntity<EventFullDto> response = restTemplate.postForEntity(
                    baseUrl + "/users/{userId}/events", request, EventFullDto.class, userId);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при создании события: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public void updateEvent(long userId, int eventId, NewEventDto newEventDto) {
        try {
            HttpEntity<NewEventDto> request = new HttpEntity<>(newEventDto);
            restTemplate.exchange(
                    baseUrl + "/users/{userId}/events/{eventId}",
                    HttpMethod.PATCH,
                    request,
                    Void.class,
                    userId,
                    eventId
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при обновлении события: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public List<EventFullDto> getUserEvents(long userId, int from, int size) {
        try {
            String url = baseUrl + "/users/{userId}/events?from={from}&size={size}";
            EventFullDto[] response = restTemplate.getForObject(url, EventFullDto[].class, userId, from, size);
            return response != null ? Arrays.asList(response) : List.of();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении событий пользователя: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public EventFullDto getEventByUser(long userId, long eventId) {
        try {
            return restTemplate.getForObject(
                    baseUrl + "/users/{userId}/events/{eventId}",
                    EventFullDto.class,
                    userId,
                    eventId
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении события: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        try {
            String url = baseUrl + "/users/{userId}/events/{eventId}/requests";
            ParticipationRequestDto[] response = restTemplate.getForObject(url, ParticipationRequestDto[].class, userId, eventId);
            return response != null ? Arrays.asList(response) : List.of();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении заявок: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public EventRequestStatusResult updateRequestStatus(long userId, int eventId, EventRequestStatusUpdateRequest statusUpdate) {
        try {
            HttpEntity<EventRequestStatusUpdateRequest> request = new HttpEntity<>(statusUpdate);
            ResponseEntity<EventRequestStatusResult> response = restTemplate.exchange(
                    baseUrl + "/users/{userId}/events/{eventId}/requests",
                    HttpMethod.PATCH,
                    request,
                    EventRequestStatusResult.class,
                    userId,
                    eventId
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при обновлении статусов заявок: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }
}
