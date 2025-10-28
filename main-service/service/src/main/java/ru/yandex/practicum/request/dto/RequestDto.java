package ru.yandex.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.yandex.practicum.event.dto.Status;
import java.time.LocalDateTime;


@Getter
@RequiredArgsConstructor
public class RequestDto {
    private final Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime created;

    private final Integer event;
    private final Integer requester;
    private final Status status;
}
