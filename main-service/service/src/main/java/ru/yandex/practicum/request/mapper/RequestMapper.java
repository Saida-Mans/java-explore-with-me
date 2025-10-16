package ru.yandex.practicum.request.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.request.ParticipationRequestDto;
import ru.yandex.practicum.request.model.Requests;

import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Component
public class RequestMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static ParticipationRequestDto toDto(Requests request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setEvent(request.getEvent().getId());
        dto.setRequester(request.getRequester().getId());
        dto.setCreated(request.getCreated().format(FORMATTER));
        dto.setStatus(request.getStatus());
        return dto;
    }
}
