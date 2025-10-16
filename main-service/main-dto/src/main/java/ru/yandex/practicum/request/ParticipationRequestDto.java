package ru.yandex.practicum.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.event.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private String created;
    private int event;
    private int id;
    private long requester;
    private Status status;
}
