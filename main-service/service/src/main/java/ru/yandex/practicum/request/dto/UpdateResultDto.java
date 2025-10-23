package ru.yandex.practicum.request.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class UpdateResultDto {

    private final List<RequestDto> confirmedRequests;
    private final List<RequestDto> rejectedRequests;
}
