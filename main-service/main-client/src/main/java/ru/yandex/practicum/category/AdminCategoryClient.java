package ru.yandex.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
@RequiredArgsConstructor
public class AdminCategoryClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AdminCategoryClient(@Value("${main-service.url}") String mainServiceUrl,
                               RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
        this.baseUrl = mainServiceUrl + "/admin/categories";
    }

    public CategoryDto create(NewCategoryDto newCategoryDto) {
        try {
            return restTemplate.postForObject(baseUrl, newCategoryDto, CategoryDto.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при создании категории: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public CategoryDto update(Long catId, NewCategoryDto newCategoryDto) {
        try {
            restTemplate.patchForObject(baseUrl + "/" + catId, newCategoryDto, CategoryDto.class);
            return getById(catId);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при обновлении категории: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public void delete(Long catId) {
        try {
            restTemplate.delete(baseUrl + "/" + catId);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при удалении категории: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public CategoryDto getById(Long catId) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + catId, CategoryDto.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении категории: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }
}
