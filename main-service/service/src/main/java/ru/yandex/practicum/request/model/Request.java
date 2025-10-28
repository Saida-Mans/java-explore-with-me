package ru.yandex.practicum.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.event.dto.Status;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.user.model.User;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@NoArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer id;

    @Setter
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @Setter
    @Enumerated(EnumType.STRING)
    private Status status;

    public Request(Event event, User requester) {
        this.event = event;
        this.requester = requester;
    }
}