package ru.yandex.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.comments.dto.CommentDto;
import ru.yandex.practicum.event.dto.State;
import ru.yandex.practicum.event.model.Event;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Page<Event> findByInitiatorId(Integer initiatorId, Pageable pageable);

    @Query("SELECT e "
            + "FROM Event e " +
            "WHERE (:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and e.eventDate >= :rangeStart " +
            "and e.eventDate <= :rangeEnd " +
            "ORDER BY e.eventDate desc")
    Page<Event> getListByAdminWithDates(@Param("users") List<Integer> users,
                                        @Param("states") List<State> states,
                                        @Param("categories") List<Integer> categories,
                                        @Param("rangeStart") LocalDateTime rangeStart,
                                        @Param("rangeEnd") LocalDateTime rangeEnd,
                                        Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "ORDER BY e.eventDate desc")
    Page<Event> getListByAdminWithoutDates(@Param("users") List<Integer> users,
                                           @Param("states") List<State> states,
                                           @Param("categories") List<Integer> categories,
                                           Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.state = :state " +
            "AND e.eventDate >= :rangeStart " +
            "AND e.eventDate <= :rangeEnd " +
            "AND (:lowerText = '' " +
            "     OR lower(e.annotation) LIKE :lowerText " +
            "     OR lower(e.description) LIKE :lowerText) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:onlyAvailable = FALSE OR e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit)")
    Page<Event> getEventsByPublicWithDates(@Param("state") State state,
                                           @Param("lowerText") String lowerText,
                                           @Param("categories") List<Integer> categories,
                                           @Param("paid") Boolean paid,
                                           @Param("rangeStart") LocalDateTime rangeStart,
                                           @Param("rangeEnd") LocalDateTime rangeEnd,
                                           @Param("onlyAvailable") boolean onlyAvailable,
                                           Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.state = :state " +
            "AND e.eventDate > CURRENT_TIMESTAMP " +
            "AND (:lowerText = '' " +
            "     OR lower(e.annotation) LIKE :lowerText " +
            "     OR lower(e.description) LIKE :lowerText) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:onlyAvailable = FALSE OR e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit)")
    List<Event> getEventsByPublicWithoutDates(@Param("state") State state,
                                              @Param("lowerText") String lowerText,
                                              @Param("categories") List<Integer> categories,
                                              @Param("paid") Boolean paid,
                                              @Param("onlyAvailable") boolean onlyAvailable,
                                              Pageable pageable);

    @Modifying
    @Query("UPDATE Event e SET e.confirmedRequests = e.confirmedRequests + :delta " +
            "WHERE e.id = :eventId")
    void incConfirmed(@Param("eventId") Integer eventId,
                      @Param("delta") int delta);

    Boolean existsByCategoryId(Integer categoryId);
}