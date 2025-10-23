package ru.yandex.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.StatsClient;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsConnector {
    private final StatsClient statsClient;

    public void sendHitEvents(HttpServletRequest request) {
        String fullUri = request.getRequestURI();
        if (request.getQueryString() != null) {
            fullUri = fullUri + "?" + request.getQueryString();
        }
        CreateEndpointHitDto hitWriteDto = new CreateEndpointHitDto("main-service",
                fullUri,
                request.getRemoteAddr(),
                LocalDateTime.now());
        try {
            statsClient.sendHit(hitWriteDto);
        } catch (Exception exc) {
            log.warn("Hit по events не прошёл !");
        }
    }

    public void sendHitEventWithId(HttpServletRequest request) {
        CreateEndpointHitDto hitWriteDto = new CreateEndpointHitDto("main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
        try {
            statsClient.sendHit(hitWriteDto);
        } catch (Exception exc) {
            log.warn("Hit по eventsId не прошёл !");
        }
    }
}

