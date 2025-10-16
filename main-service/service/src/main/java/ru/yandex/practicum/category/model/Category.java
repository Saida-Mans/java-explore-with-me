package ru.yandex.practicum.category.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.event.model.Event;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany
    @JoinColumn(name = "event_id", nullable = false)
    private List<Event> events;
}
