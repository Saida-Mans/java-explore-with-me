package ru.yandex.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.event.model.Event;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByCompilation_Id(int compilationId);

    @Query("""
        SELECT e
        FROM Event e
        WHERE (:users IS NULL OR e.initiator.id IN :users)
          AND (:states IS NULL OR e.state IN :states)
          AND (:categories IS NULL OR e.category.id IN :categories)
          AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
          AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
    """)
    Page<Event> findAdminEvents(
            @Param("users") List<Long> users,
            @Param("states") List<String> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    @Query("""
        SELECT e
        FROM Event e
        WHERE e.state = 'PUBLISHED'
          AND (:text IS NULL OR 
               LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) 
               OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))
          AND (:categories IS NULL OR e.category.id IN :categories)
          AND (:paid IS NULL OR e.paid = :paid)
          AND (
                (:rangeStart IS NULL AND :rangeEnd IS NULL AND e.eventDate >= CURRENT_TIMESTAMP)
             OR (:rangeStart IS NOT NULL AND e.eventDate >= :rangeStart)
             OR (:rangeEnd IS NOT NULL AND e.eventDate <= :rangeEnd)
          )
          AND (:onlyAvailable = false OR e.participantLimit > SIZE(e.requests))
    """)
    Page<Event> findAll(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") boolean onlyAvailable,
            Pageable pageable);

    Page<Event> findByInitiatorId(long userId, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT e FROM Event e WHERE e.initiator.id = :userId AND e.id = :eventId")
    Optional<Event> findByUserIdAndEventId(@Param("userId") long userId,
                                           @Param("eventId") long eventId);
}

