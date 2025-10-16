package ru.yandex.practicum.compilation;

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
public class PublicCompilationClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PublicCompilationClient(@Value("${main-service.url}") String mainServiceUrl,
                                   RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
        this.baseUrl = mainServiceUrl + "/compilations";
    }

    public List<CompilationDto> getAll(NewCompilation params) {
        try {
            StringBuilder urlBuilder = new StringBuilder(baseUrl)
                    .append("?from=").append(params.getFrom())
                    .append("&size=").append(params.getSize());

            if (params.getPinned() != null) {
                urlBuilder.append("&pinned=").append(params.getPinned());
            }

            CompilationDto[] response = restTemplate.getForObject(urlBuilder.toString(), CompilationDto[].class);
            return response != null ? Arrays.asList(response) : List.of();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении списка компиляций: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }

    public CompilationDto getById(Long compId) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + compId, CompilationDto.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Ошибка при получении компиляции: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Сервис недоступен: " + e.getMessage(), e);
        }
    }
}
