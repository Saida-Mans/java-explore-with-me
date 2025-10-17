package ru.yandex.practicum.category.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.category.CategoryDto;
import ru.yandex.practicum.category.NewCategoryDto;
import ru.yandex.practicum.category.model.Category;

@Component
@AllArgsConstructor
public class CategoryMapper {

    public static Category mapToCategory(NewCategoryDto newCategoryDto) {
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }

    public static CategoryDto maptoCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }
}
