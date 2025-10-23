package ru.yandex.practicum.request.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.dto.State;
import ru.yandex.practicum.event.dto.Status;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.request.dto.RequestDto;
import ru.yandex.practicum.request.dto.RequestListDto;
import ru.yandex.practicum.request.dto.UpdateResultDto;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;
import ru.yandex.practicum.request.repository.RequestRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public RequestDto saveRequest(Integer eventId, Integer requesterId) {
        if (requestRepository.existsByEventIdAndRequesterId(eventId, requesterId)) {
            throw new ValidationException("Такой запрос уже есть !", HttpStatus.CONFLICT);
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Нет такого события !"));
        if (event.getState() != State.PUBLISHED) {
            throw new ValidationException("Событие не опубликовано !", HttpStatus.CONFLICT);
        }
        if (event.getParticipantLimit() > 0 && !(event.getConfirmedRequests() < event.getParticipantLimit())) {
            throw new ValidationException("У события уже достигнут лимит на участие !", HttpStatus.CONFLICT);
        }
        User requester = userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException("Нет такого пользователя !"));
        if (event.getInitiator().getId().equals(requester.getId())) {
            throw new ValidationException("Пользователь пытается добавить запрос на участие в своём же событии !",
                    HttpStatus.CONFLICT);
        }
        Request request = new Request(event, requester);
        request.setCreated(LocalDateTime.now());
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            request.setStatus(Status.CONFIRMED);
            eventRepository.incConfirmed(eventId, 1);
        } else {
            request.setStatus(Status.PENDING);
        }
        requestRepository.save(request);
        return new RequestDto(request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByUserId(Integer requesterId) {
        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException("Нет такого пользователя !");
        }
        return requestRepository.getRequestsByUserId(requesterId);
    }

    @Override
    public RequestDto cancelRequestByRequester(Integer requesterId, Integer requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Нет такого запроса !"));
        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException("Нет такого пользователя !");
        }
        if (!request.getRequester().getId().equals(requesterId)) {
            throw new ValidationException("Пользователь хочет отменить участик не в своём запросе !", HttpStatus.CONFLICT);
        }
        request.setStatus(Status.CANCELED);
        return new RequestDto(request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByEventId(Integer eventId, Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Нет такого пользователя !");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Нет такого события !");
        }
        return requestRepository.getRequestsByEventId(eventId);
    }

    @Override
    public UpdateResultDto acceptRequestByEventInitiator(RequestListDto requestListDto, Integer userId, Integer eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Нет такого пользователя !");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Нет такого события !"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Пользователь - не инициатор события !", HttpStatus.CONFLICT);
        }
        if (event.getParticipantLimit().equals(0) || !event.getRequestModeration()) {
            throw new ValidationException("Не требуется подтверждения заявок", HttpStatus.CONFLICT);
        }
        int limit = event.getParticipantLimit() - event.getConfirmedRequests();
        if (limit == 0) {
            throw new ValidationException("Лимит исчерпан !", HttpStatus.CONFLICT);
        }
        List<Request> pendingRequests = requestRepository.findAllByIdInAndEventIdAndStatus(requestListDto.getRequestIds(),
                eventId,
                Status.PENDING);
        if (pendingRequests.size() != requestListDto.getRequestIds().size()) {
            throw new ValidationException("Не все заявки имеют статус PENDING !", HttpStatus.CONFLICT);
        }
        List<Integer> idsWithLimit  = requestListDto.getRequestIds()
                .subList(0, Math.min(limit, requestListDto.getRequestIds().size()));
        List<Integer> idsAfterLimit = requestListDto.getRequestIds()
                .subList(Math.min(limit,requestListDto.getRequestIds().size()), requestListDto.getRequestIds().size());
        requestRepository.updateStatusByIdsAndEventId(requestListDto.getStatus(), idsWithLimit, eventId);
        if (requestListDto.getStatus() == Status.CONFIRMED) {
            eventRepository.incConfirmed(eventId, idsWithLimit.size());
        }
        requestRepository.updateStatusByIdsAndEventId(Status.REJECTED, idsAfterLimit, eventId);
        return new UpdateResultDto(
                requestRepository.getByStatusAndEventIdAndRequestsIds(requestListDto.getRequestIds(), eventId, Status.CONFIRMED),
                requestRepository.getByStatusAndEventIdAndRequestsIds(requestListDto.getRequestIds(), eventId, Status.REJECTED));
    }
}