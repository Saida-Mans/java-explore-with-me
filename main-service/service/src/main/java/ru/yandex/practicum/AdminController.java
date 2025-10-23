package ru.yandex.practicum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;
import ru.yandex.practicum.category.service.CategoryService;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.service.CompilationService;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.State;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final CompilationService compilationService;
    private final EventService eventService;

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) List<Integer> ids,
                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        if (ids == null) {
            return userService.getUsersByOffset(from, size);
        }
        return userService.getUsersByListId(ids);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@RequestBody @Valid NewUserRequest userWriteDto) {
        return userService.saveUser(userWriteDto);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") Integer userId) {
        userService.deleteUser(userId);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveCategory(@RequestBody @Valid NewCategoryDto categoryWriteDto) {
        return categoryService.saveCategory(categoryWriteDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Integer catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable("catId") Integer catId,
                                          @RequestBody @Valid NewCategoryDto categoryWriteDto) {
        return categoryService.updateCategory(catId, categoryWriteDto);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto saveCompilation(@RequestBody @Validated(NewCompilationDto.OnPost.class)
                                          NewCompilationDto compilationWriteDto) {
        return compilationService.saveCompilation(compilationWriteDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") Integer compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable("compId") Integer compId,
                                                @RequestBody @Validated(NewCompilationDto.OnPatch.class)
                                                NewCompilationDto compilationWriteDto) {
        return compilationService.updateCompilation(compilationWriteDto, compId);
    }

    @GetMapping("/events")
    public List<EventFullDto> getUsers(@RequestParam(required = false) List<Integer> users,
                                       @RequestParam(required = false) List<State> states,
                                       @RequestParam(required = false) List<Integer> categories,
                                       @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                       @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                       @RequestParam(required = false, defaultValue = "0") Integer from,
                                       @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventService.getListByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody @Validated(NewEventDto.OnUpdateState.class) NewEventDto eventWriteDto,
                                        @PathVariable("eventId") Integer eventId) {
        return eventService.updateEventByAdmin(eventWriteDto, eventId);
    }
}

