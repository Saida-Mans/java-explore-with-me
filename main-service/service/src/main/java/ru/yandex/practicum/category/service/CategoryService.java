package ru.yandex.practicum.category.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.category.CategoryDto;
import ru.yandex.practicum.category.NewCategoryDto;
import ru.yandex.practicum.category.model.NewCategory;
import java.util.List;

public interface CategoryService {

    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(Long catId, NewCategoryDto newCategoryDto);

    void delete(@PathVariable Long catId);

    List<CategoryDto> getAll(NewCategory newCategory);

    CategoryDto getById(Long catId);
}
