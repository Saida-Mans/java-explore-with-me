package ru.yandex.practicum.comments.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.comments.dto.CommentDto;
import ru.yandex.practicum.comments.dto.NewCommentsRequest;
import ru.yandex.practicum.comments.dto.UpdateCommentDto;
import ru.yandex.practicum.comments.mapper.CommentMapper;
import ru.yandex.practicum.comments.repository.CommentRepository;
import ru.yandex.practicum.event.dto.State;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;
import ru.yandex.practicum.comments.model.Comment;
import java.util.List;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public CommentDto createComment(Integer userId, Integer eventId, NewCommentsRequest newCommentsRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (State.PUBLISHED != event.getState())
            throw new NotFoundException("Нельзя комментировать неопубликованное событие");
        Comment comment = commentMapper.toComment(event, user, newCommentsRequest);
        commentRepository.save(comment);
        CommentDto commentDto = commentMapper.toCommentDto(comment);
        return commentDto;
    }

    @Transactional
    @Override
    public CommentDto updateComment(Integer userId, Integer eventId, Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentRepository.findByIdAndUser_IdAndEvent_Id(commentId, userId, eventId).orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        if (updateCommentDto.getText() != null && !updateCommentDto.getText().isBlank()) {
            comment.setText(updateCommentDto.getText());
        }
        CommentDto commentDto = commentMapper.toCommentDto(comment);
        return commentDto;
    }

    @Transactional
    @Override
    public void deleteComment(Integer userId, Long commentId) {
        commentRepository.findByIdAndUser_Id(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        commentRepository.deleteById(commentId);
    }

    @Transactional
    @Override
    public void deleteCommentAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsForEvent(Integer eventId, Integer from, Integer size) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        List<CommentDto> commentDtos = commentRepository.findByEventId(event.getId(), pageable).getContent();
        return commentDtos;
    }

    @Override
    public List<CommentDto> getCommentsForUser(Integer userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        List<CommentDto> commentDtos = commentRepository.findByUserId(user.getId(), pageable).getContent();
        return commentDtos;
    }
}
