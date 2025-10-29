package ru.yandex.practicum.comments.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.comments.dto.CommentDto;
import ru.yandex.practicum.comments.dto.NewCommentsRequest;
import ru.yandex.practicum.comments.model.Comment;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.user.model.User;
import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public Comment toComment(Event event, User user, NewCommentsRequest newCommentsRequest) {
        Comment comment = new Comment();
        comment.setEvent(event);
        comment.setUser(user);
        comment.setText(newCommentsRequest.getText());
        comment.setCreatedDate(LocalDateTime.now());
        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setEventId(comment.getEvent().getId());
        commentDto.setUserId(comment.getUser().getId());
        commentDto.setText(comment.getText());
        commentDto.setCreatedDate(comment.getCreatedDate());
        return commentDto;
    }
}
