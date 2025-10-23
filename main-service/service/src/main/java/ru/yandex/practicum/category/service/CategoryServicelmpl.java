package ru.yandex.practicum.category.service;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.category.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CategoryServicelmpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto saveCategory(NewCategoryDto categoryWriteDto) {
        if (categoryRepository.existsByName(categoryWriteDto.getName())) {
            throw new ValidationException("Такая категория уже существует !", HttpStatus.CONFLICT);
        }
        Category category = new Category();
        category.setName(categoryWriteDto.getName());
        categoryRepository.save(category);
        return CategoryMapper.categorytoCategoryReadDto(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Такой категории не существует !");
        }
        if (eventRepository.existsByCategoryId(id)) {
            throw new ValidationException("Нельзя удалить категории, с которой связаны события !", HttpStatus.CONFLICT);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto updateCategory(Integer id, NewCategoryDto categoryWriteDto) {
        if (categoryRepository.existsByNameAndIdNot(categoryWriteDto.getName(), id)) {
            throw new ValidationException("Такая категория уже существует !", HttpStatus.CONFLICT);
        }
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Такой категории не сущетсвует"));
        category.setName(categoryWriteDto.getName());
        return CategoryMapper.categorytoCategoryReadDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.getCategoriesByOffset(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Такой категории не существуют !"));
        return new CategoryDto(category.getId(), category.getName());
    }
}
