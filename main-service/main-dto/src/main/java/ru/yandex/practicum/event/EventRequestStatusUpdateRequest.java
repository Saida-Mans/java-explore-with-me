package ru.yandex.practicum.event;

import lombok.Data;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private Status status;
}
