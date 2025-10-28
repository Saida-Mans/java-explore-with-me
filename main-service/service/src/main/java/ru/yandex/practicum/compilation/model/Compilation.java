package ru.yandex.practicum.compilation.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.event.model.Event;
import java.util.Set;

@Getter
@Entity
@Table(name = "compilations")
@NoArgsConstructor
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Integer id;

    @Setter
    private String title;

    @Setter
    private Boolean pinned;

    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events;

    public Compilation(Set<Event> events) {
        this.events = events;
    }
}
