package ru.yandex.practicum.event;

import lombok.Data;
import org.springframework.beans.factory.parsing.Location;

@Data
public class UpdateEventUserRequest {
    private String annotation;
    private int category;
    private String description;
    private String eventDate;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private Action action;
    private String title;
}
