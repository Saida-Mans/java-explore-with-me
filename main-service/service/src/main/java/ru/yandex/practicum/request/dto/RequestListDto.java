package ru.yandex.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.event.dto.Status;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestListDto {

    private List<Integer> requestIds;

    private Status status;
}
