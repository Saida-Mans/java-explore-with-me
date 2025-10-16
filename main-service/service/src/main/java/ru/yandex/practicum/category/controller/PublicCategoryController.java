package ru.yandex.practicum.category.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.category.CategoryDto;
import ru.yandex.practicum.category.model.NewCategory;
import ru.yandex.practicum.category.service.CategoryService;
import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        NewCategory newCategory = new NewCategory(from, size);
        return categoryService.getAll(newCategory);
    }

    @GetMapping("{catId}")
    public CategoryDto getById(@PathVariable Long catId) {
        return categoryService.getById(catId);
    }
}
