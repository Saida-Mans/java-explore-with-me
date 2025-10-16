package ru.yandex.practicum.user;

import org.springframework.http.HttpEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class AdminUserClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AdminUserClient(@Value("${admin-user.url}") String serverUrl, RestTemplateBuilder builder) {
        this.baseUrl = serverUrl;
        this.restTemplate = builder.build();
    }

    public NewUserDto createUser(NewUserRequest request) {
        try {
            HttpEntity<NewUserRequest> entity = new HttpEntity<>(request);
            ResponseEntity<NewUserDto> response = restTemplate.exchange(
                    baseUrl + "/admin/users",
                    HttpMethod.POST,
                    entity,
                    NewUserDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Некорректные данные для создания пользователя: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException.Conflict e) {
            throw new IllegalStateException("Пользователь уже существует: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при вызове AdminUserController: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("AdminUserController недоступен: " + e.getMessage(), e);
        }
    }

    public List<NewUserDto> getUsers(List<Long> ids, int from, int size) {
        try {
            StringBuilder url = new StringBuilder(baseUrl + "/admin/users?from=" + from + "&size=" + size);
            if (ids != null && !ids.isEmpty()) {
                ids.forEach(id -> url.append("&ids=").append(id));
            }
            ResponseEntity<NewUserDto[]> response = restTemplate.getForEntity(url.toString(), NewUserDto[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Пользователи не найдены: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении пользователей: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("AdminUserController недоступен: " + e.getMessage(), e);
        }
    }

    public void deleteUserById(Long userId) {
        try {
            restTemplate.delete(baseUrl + "/admin/users/{id}", userId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Пользователь не найден: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при удалении пользователя: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("AdminUserController недоступен: " + e.getMessage(), e);
        }
    }
}
