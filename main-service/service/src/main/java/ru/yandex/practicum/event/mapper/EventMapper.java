package ru.yandex.practicum.event.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.dto.*;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.model.User;
import java.time.LocalDateTime;

@AllArgsConstructor
@Component
public class EventMapper {

    public static EventFullDto eventToFullDto(Event event, Long views) {
        return new EventFullDto(event.getId(),
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                new UserDto(event.getInitiator().getId(),
                        event.getInitiator().getEmail(),
                        event.getInitiator().getName()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                views);
    }

    public static EventShortDto eventToShortDto(Event event, Integer views) {
        return new EventShortDto(event.getId(),
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                new UserDto(event.getInitiator().getId(),
                        event.getInitiator().getEmail(),
                        event.getInitiator().getName()),
                event.getPaid(),
                event.getTitle(),
                views);
    }

    public static Event saveEventWriteDtoToEvent(NewEventDto eventWriteDto,
                                                 Category category,
                                                 User initiator) {
        Event event = new Event(category, initiator);
        event.setAnnotation(eventWriteDto.getAnnotation());
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(eventWriteDto.getDescription());
        event.setEventDate(eventWriteDto.getEventDate());
        event.setLocation(eventWriteDto.getLocation());
        if (eventWriteDto.getPaid() == null) {
            event.setPaid(false);
        } else {
            event.setPaid(eventWriteDto.getPaid());
        }
        if (eventWriteDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        } else {
            event.setParticipantLimit(eventWriteDto.getParticipantLimit());
        }
        if (eventWriteDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        } else {
            event.setRequestModeration(eventWriteDto.getRequestModeration());
        }
        event.setTitle(eventWriteDto.getTitle());
        event.setState(State.PENDING);
        event.setPublishedOn(null);
        event.setConfirmedRequests(0);
        return event;
    }
}
