package ru.yandex.practicum.user.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Setter
    @Column(unique = true)
    private String email;

    @Setter
    private String name;
}