package ru.yandex.practicum.user;

import lombok.Data;

@Data
public class NewUserDto {
    private Long id;
    private String email;
    private String name;
}
