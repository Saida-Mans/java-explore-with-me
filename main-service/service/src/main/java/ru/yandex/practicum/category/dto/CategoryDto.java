package ru.yandex.practicum.category.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CategoryDto {
    private final Integer id;
    private final String name;
}
