package ru.yandex.practicum.event.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.comments.model.Comment;
import ru.yandex.practicum.event.dto.State;
import ru.yandex.practicum.user.model.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "events")
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer id;

    @Setter
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Setter
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;

    @Setter
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Setter
    private String description;

    @Setter
    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Setter
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "lat", column = @Column(name = "lat", nullable = false)),
            @AttributeOverride(name = "lon", column = @Column(name = "lon", nullable = false))})
    private Location location;

    @Setter
    private Boolean paid;

    @Setter
    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Setter
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Setter
    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Setter
    @Enumerated(EnumType.STRING)
    private State state;

    @Setter
    private String title;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "event")
    private List<Comment> comments = new ArrayList<>();

    public Event(Category category, User initiator) {
        this.category = category;
        this.initiator = initiator;
    }
}