package ru.yandex.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.event.dto.Status;
import ru.yandex.practicum.request.dto.RequestDto;
import ru.yandex.practicum.request.model.Request;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    Boolean existsByEventIdAndRequesterId(Integer eventId, Integer requestId);

    @Query("SELECT new ru.yandex.practicum.request.dto.RequestDto(r.id, " +
            "r.created, " +
            "r.event.id, " +
            "r.requester.id, " +
            "r.status) " +
            "FROM Request r " +
            "WHERE :requesterId is null or r.requester.id = :requesterId")
    List<RequestDto> getRequestsByUserId(@Param("requesterId") Integer requesterId);

    @Query("SELECT new ru.yandex.practicum.request.dto.RequestDto(r.id, " +
            "r.created, " +
            "r.event.id, " +
            "r.requester.id, " +
            "r.status) " +
            "FROM Request r " +
            "WHERE :eventId is null or r.event.id = :eventId")
    List<RequestDto> getRequestsByEventId(@Param("eventId") Integer eventId);

    List<Request> findAllByIdInAndEventIdAndStatus(List<Integer> ids, Integer eventId, Status status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Request r " +
            "SET r.status = :status " +
            "WHERE r.id IN :ids " +
            "AND r.event.id = :eventId")
    void updateStatusByIdsAndEventId(@Param("status") Status status,
                                     @Param("ids") List<Integer> ids,
                                     @Param("eventId") Integer eventId);

    @Query("SELECT new ru.yandex.practicum.request.dto.RequestDto(r.id, " +
            "r.created, " +
            "r.event.id, " +
            "r.requester.id, " +
            "r.status) " +
            "FROM Request r " +
            "WHERE (:eventId is null or r.event.id = :eventId) " +
            "and (:status is null or r.status = :status) " +
            "and r.id in :ids")
    List<RequestDto> getByStatusAndEventIdAndRequestsIds(@Param("ids") List<Integer> ids,
                                                         @Param("eventId") Integer eventId,
                                                         @Param("status") Status status);
}