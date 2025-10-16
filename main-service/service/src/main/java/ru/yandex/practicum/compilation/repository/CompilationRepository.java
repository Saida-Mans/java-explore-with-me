package ru.yandex.practicum.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c FROM Compilation c")
    Page<Compilation> getAll(Pageable pageable);

    boolean existsByTitle(String title);

    @Query("SELECT c FROM Compilation c WHERE c.pinned = :pinned")
    Page<Compilation> getAllByPinned(@Param("pinned") boolean pinned, Pageable pageable);
}
