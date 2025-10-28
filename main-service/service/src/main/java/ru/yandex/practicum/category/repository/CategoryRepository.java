package ru.yandex.practicum.category.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);

    @Query("SELECT new ru.yandex.practicum.category.dto.CategoryDto(c.id, c.name) " +
            "from Category c " +
            "ORDER BY c.id asc")
    List<CategoryDto> getCategoriesByOffset(Pageable pageable);
}

