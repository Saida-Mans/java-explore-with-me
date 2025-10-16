package ru.yandex.practicum.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.event.EventFullDto;
import ru.yandex.practicum.event.State;
import ru.yandex.practicum.event.UpdateEventAdminRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class AdminEventClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AdminEventClient(@Value("${admin-event.url}") String serverUrl, RestTemplateBuilder builder) {
        this.baseUrl = serverUrl;
        this.restTemplate = builder.build();
    }

    public List<EventFullDto> getAll(List<Long> users,
                                     List<State> states,
                                     List<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     int from,
                                     int size) {
        try {
            StringBuilder urlBuilder = new StringBuilder(baseUrl + "/admin/events?from=" + from + "&size=" + size);

            if (users != null && !users.isEmpty()) {
                users.forEach(id -> urlBuilder.append("&users=").append(id));
            }
            if (states != null && !states.isEmpty()) {
                states.forEach(state -> urlBuilder.append("&states=").append(state.name()));
            }
            if (categories != null && !categories.isEmpty()) {
                categories.forEach(cat -> urlBuilder.append("&categories=").append(cat));
            }
            if (rangeStart != null) urlBuilder.append("&rangeStart=").append(rangeStart);
            if (rangeEnd != null) urlBuilder.append("&rangeEnd=").append(rangeEnd);

            ResponseEntity<EventFullDto[]> response = restTemplate.getForEntity(urlBuilder.toString(), EventFullDto[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("События не найдены: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении событий: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис AdminEventController недоступен: " + e.getMessage(), e);
        }
    }

    public void update(int eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        try {
            String url = baseUrl + "/admin/events/{eventId}";
            HttpEntity<UpdateEventAdminRequest> request = new HttpEntity<>(updateEventAdminRequest);
            restTemplate.exchange(url, HttpMethod.PATCH, request, Void.class, eventId);
        } catch (HttpClientErrorException.Conflict e) {
            throw new RuntimeException("Событие не может быть обновлено: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Событие не найдено: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при обновлении события: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис AdminEventController недоступен: " + e.getMessage(), e);
        }
    }
}