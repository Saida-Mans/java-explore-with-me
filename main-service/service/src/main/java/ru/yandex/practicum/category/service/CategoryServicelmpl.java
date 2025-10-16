package ru.yandex.practicum.category.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.CategoryDto;
import ru.yandex.practicum.category.model.NewCategory;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.category.NewCategoryDto;
import ru.yandex.practicum.category.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.exception.ConflictException;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServicelmpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Категория с таким именем уже существует");
        }

        Category category = CategoryMapper.mapToCategory(newCategoryDto);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.maptoCategoryDto(saved);
    }

    @Transactional
    public CategoryDto update(Long catId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        if (categoryRepository.existsByName(newCategoryDto.getName())
                && !category.getName().equals(newCategoryDto.getName())) {
            throw new ConflictException("Категория с таким именем уже существует");
        }
        category.setName(newCategoryDto.getName());
        Category updated = categoryRepository.save(category);
        return CategoryMapper.maptoCategoryDto(updated);
    }

    @Transactional
    public void delete(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        boolean hasEvents = eventRepository.existsByCategoryId(catId);
        if (hasEvents) {
            throw new ConflictException("Существуют события, связанные с категорией");
        }
        categoryRepository.delete(category);
    }

    public List<CategoryDto> getAll(NewCategory newCategory) {
        Pageable pageable = PageRequest.of(newCategory.getFrom() / newCategory.getSize(), newCategory.getSize());
        Page<Category> page = categoryRepository.findAllCategories(pageable);
        return page.stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .toList();
    }

    public CategoryDto getById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Категория не найдена или недоступна"));
        CategoryDto categoryDto = CategoryMapper.maptoCategoryDto(category);
        return categoryDto;
    }
}
