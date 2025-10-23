package ru.yandex.practicum.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.compilation.model.Compilation;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    @EntityGraph(attributePaths = "events")
    @Query(value = "SELECT c " +
            "FROM Compilation c " +
            "WHERE (:pinned IS NULL OR c.pinned = :pinned)")
    Page<Compilation> getCompilationListByPublic(@Param("pinned") Boolean pinned,
                                                 Pageable pageable);

    @EntityGraph(attributePaths = {"events", "events.category", "events.initiator"})
    @Query("SELECT c FROM Compilation c WHERE c.id = :id")
    Optional<Compilation> findByIdWithGraph(@Param("id") Integer id);
}
