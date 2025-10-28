package ru.yandex.practicum.category.mapper;

import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.dto.NewCategoryDto;
import ru.yandex.practicum.category.dto.CategoryDto;

public class CategoryMapper {

    public static Category categoryWriteDtoToCategory(NewCategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return category;
    }

    public static CategoryDto categorytoCategoryReadDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
