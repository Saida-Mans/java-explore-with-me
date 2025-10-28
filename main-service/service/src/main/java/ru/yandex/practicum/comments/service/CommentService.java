package ru.yandex.practicum.comments.service;

import ru.yandex.practicum.comments.dto.CommentDto;
import ru.yandex.practicum.comments.dto.NewCommentsRequest;
import ru.yandex.practicum.comments.dto.UpdateCommentDto;
import java.util.List;

public interface CommentService {

    CommentDto createComment(Integer userId, Integer eventId, NewCommentsRequest newCommentsRequest);

    CommentDto updateComment(Integer userId, Integer eventId, Long commentId, UpdateCommentDto updateCommentDto);

    void deleteComment(Integer userId, Long commentId);

    void deleteCommentAdmin(Long commentId);

    List<CommentDto> getCommentsForEvent(Integer eventId, Integer from, Integer size);

    List<CommentDto> getCommentsForUser(Integer userId, Integer from, Integer size);
}
