package ru.yandex.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.request.model.Requests;
import java.util.List;

public interface RequestRepository extends JpaRepository<Requests, Long> {

    boolean existsByRequesterIdAndEventId(long userId, long eventId);

    @Query("SELECT r FROM Requests r WHERE r.requester.id = :userId")
    List<Requests> getUserRequests(@Param("userId") long userId);

    @Query("""
       SELECT r
       FROM Requests r
       WHERE r.event.initiator.id = :userId
       AND r.event.id = :eventId
       """)
    List<Requests> getRequests(@Param("userId") long userId, @Param("eventId") long eventId);
}
