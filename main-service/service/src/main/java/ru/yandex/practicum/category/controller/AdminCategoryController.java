package ru.yandex.practicum.category.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.category.CategoryDto;
import ru.yandex.practicum.category.NewCategoryDto;
import ru.yandex.practicum.category.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@AllArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.create(newCategoryDto);
    }

    @PatchMapping("{catId}")
    public CategoryDto update(@PathVariable Long catId, @RequestBody NewCategoryDto newCategoryDto) {
      return categoryService.update(catId, newCategoryDto);
    }

    @DeleteMapping("{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        categoryService.delete(catId);
    }
}
