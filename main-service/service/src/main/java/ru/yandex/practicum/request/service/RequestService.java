package ru.yandex.practicum.request.service;

import ru.yandex.practicum.request.dto.RequestDto;
import ru.yandex.practicum.request.dto.RequestListDto;
import ru.yandex.practicum.request.dto.UpdateResultDto;
import java.util.List;

public interface RequestService {

    RequestDto saveRequest(Integer eventId, Integer requesterId);

    List<RequestDto> getRequestsByUserId(Integer requesterId);

    RequestDto cancelRequestByRequester(Integer requesterId, Integer requestId);

    List<RequestDto> getRequestsByEventId(Integer eventId, Integer userId);

    UpdateResultDto acceptRequestByEventInitiator(RequestListDto requestListDto, Integer userId, Integer eventId);
}
