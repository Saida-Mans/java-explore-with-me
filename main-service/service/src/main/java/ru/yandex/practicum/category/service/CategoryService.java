package ru.yandex.practicum.category.service;

import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;
import java.util.List;

public interface CategoryService {

    CategoryDto saveCategory(NewCategoryDto categoryWriteDto);

    void deleteCategory(Integer id);

    CategoryDto updateCategory(Integer id, NewCategoryDto categoryWriteDto);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Integer id);
}
