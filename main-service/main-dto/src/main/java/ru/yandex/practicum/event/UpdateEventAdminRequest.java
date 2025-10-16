package ru.yandex.practicum.event;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {
    private String annotation;
    private long category;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private Action action;
    private String title;
}
