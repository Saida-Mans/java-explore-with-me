package ru.yandex.practicum.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
@RequiredArgsConstructor
public class AdminCompilationClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AdminCompilationClient(@Value("${main-service.url}") String mainServiceUrl,
                                  RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
        this.baseUrl = mainServiceUrl + "/admin/compilations";
    }

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        try {
            return restTemplate.postForObject(baseUrl, newCompilationDto, CompilationDto.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при создании компиляции: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public CompilationDto updateCompilation(Long compId, NewCompilationDto newCompilationDto) {
        try {
            HttpEntity<NewCompilationDto> request = new HttpEntity<>(newCompilationDto);
            ResponseEntity<CompilationDto> response = restTemplate.exchange(
                    baseUrl + "/" + compId,
                    HttpMethod.PATCH,
                    request,
                    CompilationDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при обновлении компиляции: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public void deleteCompilation(Long compId) {
        try {
            restTemplate.delete(baseUrl + "/" + compId);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при удалении компиляции: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }
}
