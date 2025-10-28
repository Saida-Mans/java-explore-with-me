package ru.yandex.practicum.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.CreateEndpointHitDto;
import ru.yandex.practicum.ViewStatsDto;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class StatsClient {
    private final RestTemplate rest;
    private final String baseUrl;

    public StatsClient(RestTemplateBuilder builder, @Value("${stats.url}") String baseUrl) {
        this.baseUrl = baseUrl;
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        this.rest = builder.uriTemplateHandler(factory).build();
    }

    public void sendHit(CreateEndpointHitDto dto) {
        rest.postForEntity("/hit", dto, Void.class);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String s = fmt.format(start).replace(" ", "%20");
        String e = fmt.format(end).replace(" ", "%20");
        StringBuilder qs = new StringBuilder("start=").append(s)
                .append("&end=").append(e)
                .append("&unique=").append(unique);
        if (uris != null && !uris.isEmpty()) {
            for (String u : uris) {
                String enc = u.replace(" ", "%20");
                qs.append("&uris=").append(enc);
            }
        }
        URI uri = URI.create(baseUrl + "/stats?" + qs);
        ResponseEntity<ViewStatsDto[]> r = rest.getForEntity(uri, ViewStatsDto[].class);
        if (r.getBody() == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(r.getBody());
        }
        }
    }

