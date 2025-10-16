package ru.yandex.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCategoryClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PublicCategoryClient(@Value("${main-service.url}") String mainServiceUrl,
                                RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
        this.baseUrl = mainServiceUrl + "/categories";
    }

    public List<CategoryDto> getAll(NewCategory params) {
        try {
            String url = baseUrl + "?from=" + params.getFrom() + "&size=" + params.getSize();
            CategoryDto[] response = restTemplate.getForObject(url, CategoryDto[].class);
            return response != null ? Arrays.asList(response) : List.of();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении категорий: " + e.getMessage(), e);
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
