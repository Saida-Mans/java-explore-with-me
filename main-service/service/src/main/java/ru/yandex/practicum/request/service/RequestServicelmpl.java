package ru.yandex.practicum.request.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.State;
import ru.yandex.practicum.event.Status;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.ParticipationRequestDto;
import ru.yandex.practicum.request.mapper.RequestMapper;
import ru.yandex.practicum.request.model.Requests;
import ru.yandex.practicum.request.repository.RequestRepository;
import ru.yandex.practicum.user.repository.UserRepository;
import ru.yandex.practicum.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class RequestServicelmpl implements RequestService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(long userId, int eventId) {
     User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
     Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
     if (event.getInitiator().equals(user)) {
        new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
     }
     if (event.getState() != State.PUBLISHED) {
         new ConflictException("Нельзя участвовать в неопубликованном событии");
     }
     if (event.getRequests().size() >= event.getParticipantLimit()) {
         throw new ConflictException("Достигнут лимит участников события");
     }
        boolean alreadyRequested = requestRepository.existsByRequesterIdAndEventId(userId, eventId);
        if (alreadyRequested) {
            throw new ConflictException("Запрос на участие уже существует");
        }
        Status status = event.isRequestModeration() ? Status.PENDING : Status.CONFIRMED;
        Requests request = Requests.builder()
                .event(event)
                .requester(user)
                .status(status)
                .created(LocalDateTime.now())
                .build();
        requestRepository.save(request);
        return RequestMapper.toDto(request);
    }


    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Requests request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        if (!request.getRequester().equals(user)) {
            throw new NotFoundException("Запрос не найден или недоступен");
        }
        request.setStatus(Status.CANCELED);
        requestRepository.save(request);
        return RequestMapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Requests> requests = requestRepository.getUserRequests(userId);
        List<ParticipationRequestDto> dto = requests.stream()
                .map(RequestMapper::toDto)
                .toList();
        return dto;
    }
}


