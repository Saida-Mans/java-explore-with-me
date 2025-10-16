package ru.yandex.practicum.event;

import lombok.Data;
import ru.yandex.practicum.request.ParticipationRequestDto;
import java.util.List;

@Data
public class EventRequestStatusResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
