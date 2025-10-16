package ru.yandex.practicum.event.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.category.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.*;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.user.mapper.UserMapper;
import ru.yandex.practicum.user.model.User;

@AllArgsConstructor
@Component
public class EventMapper {

    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public EventShortDto toShortDto(Event event) {
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setPaid(event.isPaid());
        dto.setEventDate(event.getEventDate().toString());
        dto.setConfirmedRequests((int) event.getRequests().stream()
                .filter(r -> r.getStatus() == Status.CONFIRMED)
                .count());
        dto.setViews(event.getViews());
        dto.setCategoryDto(categoryMapper.maptoCategoryDto(event.getCategory()));
        dto.setUserShortDto(userMapper.toShortDto(event.getInitiator()));
        return dto;
}

    public static EventFullDto mapToEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setId((int) event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategoryDto(CategoryMapper.maptoCategoryDto(event.getCategory()));
        eventFullDto.setConfirmedRequests(
                event.getRequests() != null ? event.getRequests().size() : 0
        );
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setUserShortDto(UserMapper.toShortDto(event.getInitiator()));
        eventFullDto.setLocation(new Location(event.getLat(), event.getLon()));
        eventFullDto.setPaid(event.isPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.isRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());
        return eventFullDto;
    }

    public static Event toEvent(NewEventDto dto, Category category, User initiator) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setCategory(category);
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setPaid(dto.isPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.isRequestModeration());
        event.setTitle(dto.getTitle());
        event.setInitiator(initiator);
        return event;
    }

    public void updateEventFromDto(NewEventDto dto, Event event, Category category) {
        event.setAnnotation(dto.getAnnotation());
        event.setCategory(category);
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        if (dto.getLocation() != null) {
            event.setLat(dto.getLocation().getLat());
            event.setLon(dto.getLocation().getLon());
        }
        event.setPaid(dto.isPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.isRequestModeration());
        event.setTitle(dto.getTitle());
    }
}
