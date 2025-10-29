package ru.yandex.practicum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comments.dto.CommentDto;
import ru.yandex.practicum.comments.dto.NewCommentsRequest;
import ru.yandex.practicum.comments.dto.UpdateCommentDto;
import ru.yandex.practicum.comments.service.CommentService;
import java.util.List;

@Validated
@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @Positive Integer userId, @PathVariable @Positive Integer eventId, @RequestBody @Valid NewCommentsRequest newCommentsRequest) {
    return commentService.createComment(userId, eventId, newCommentsRequest);
    }

    @PatchMapping("/{eventId}/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable @Positive Integer userId, @PathVariable @Positive Integer eventId, @PathVariable @Positive Long commentId, @RequestBody @Valid UpdateCommentDto updateCommentDto) {
        return commentService.updateComment(userId, eventId, commentId, updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Integer userId,
                              @PathVariable @Positive Long commentId
    ) {
        commentService.deleteComment(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getCommentsForUser(@PathVariable @Positive Integer userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size) {
        return commentService.getCommentsForUser(userId, from, size);
    }
}
