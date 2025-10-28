package ru.yandex.practicum.comments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.comments.dto.CommentDto;
import ru.yandex.practicum.comments.model.Comment;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndUser_IdAndEvent_Id(Long commentId, Integer userId, Integer eventId);

    Optional<Comment> findByIdAndUser_Id(Long commentId, Integer userId);

    @Query("SELECT new ru.yandex.practicum.comments.dto.CommentDto(c.id, c.text, c.user.id, c.event.id, c.createdDate) " +
            "FROM Comment c " +
            "WHERE c.event.id = :eventId ")
    Page<CommentDto> findByEventId(@Param("eventId") Integer eventId, Pageable pageable);

    @Query("SELECT new ru.yandex.practicum.comments.dto.CommentDto(c.id, c.text, c.user.id, c.event.id, c.createdDate) " +
            "FROM Comment c " +
            "WHERE c.user.id = :userId ")
    Page<CommentDto> findByUserId(@Param("userId") Integer userId, Pageable pageable);
}
