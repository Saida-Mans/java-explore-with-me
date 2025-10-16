package ru.yandex.practicum.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class PrivateRequestClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PrivateRequestClient(@Value("${private-request.url}") String serverUrl, RestTemplateBuilder builder) {
        this.baseUrl = serverUrl;
        this.restTemplate = builder.build();
    }

    public ParticipationRequestDto createRequest(long userId, int eventId) {
        try {
            String url = baseUrl + "/users/{userId}/requests?eventId={eventId}";
            ResponseEntity<ParticipationRequestDto> response = restTemplate.postForEntity(
                    url, null, ParticipationRequestDto.class, userId, eventId
            );
            return response.getBody();
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Некорректный запрос на участие: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException.Conflict e) {
            throw new IllegalStateException("Невозможно создать запрос: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при вызове PrivateRequestController: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис PrivateRequestController недоступен: " + e.getMessage(), e);
        }
    }

    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        try {
            String url = baseUrl + "/users/{userId}/requests/{requestId}/cancel";
            ResponseEntity<ParticipationRequestDto> response = restTemplate.exchange(
                    url, HttpMethod.PATCH, null, ParticipationRequestDto.class, userId, requestId
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Запрос не найден: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при отмене запроса: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис PrivateRequestController недоступен: " + e.getMessage(), e);
        }
    }

    public List<ParticipationRequestDto> getUserRequests(long userId) {
        try {
            String url = baseUrl + "/users/{userId}/requests";
            ResponseEntity<ParticipationRequestDto[]> response = restTemplate.getForEntity(
                    url, ParticipationRequestDto[].class, userId
            );
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Пользователь или запросы не найдены: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении запросов: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис PrivateRequestController недоступен: " + e.getMessage(), e);
        }
    }
}
