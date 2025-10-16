package ru.yandex.practicum.request.service;

import ru.yandex.practicum.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(long userId, int eventId);
    ParticipationRequestDto cancelRequest(long userId, long requestId);
    List<ParticipationRequestDto> getUserRequests(long userId);
}
