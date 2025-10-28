package ru.yandex.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.service.CategoryService;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.service.CompilationService;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicController {
    private final CategoryService categoryService;
    private final CompilationService compilationService;
    private final EventService eventService;
    private final StatsConnector statsConnector;

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategories(@PathVariable("catId") Integer catId) {
        return categoryService.getCategoryById(catId);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilationsByPublic(@RequestParam(required = false, defaultValue = "0") Integer from,
                                                        @RequestParam(required = false, defaultValue = "10") Integer size,
                                                        @RequestParam(required = false) Boolean pinned) {
        return compilationService.getCompilationListByPublic(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationByIdByPublic(@PathVariable("compId") Integer compId) {
        return compilationService.getCompilationByIdByPublic(compId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsByPublic(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false)List<Integer> categories,
                                                 @RequestParam(required = false)Boolean paid,
                                                 @RequestParam(required = false)
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                 @RequestParam(required = false)
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                 @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable,
                                                 @RequestParam(required = false, defaultValue = "EVENT_DATE")String sort,
                                                 @RequestParam(required = false, defaultValue = "0")Integer from,
                                                 @RequestParam(required = false, defaultValue = "10")Integer size,
                                                 HttpServletRequest request) {
        List<EventShortDto> resultList = eventService.getEventsByPublic(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size);
        statsConnector.sendHitEvents(request);
        return resultList;
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventByIdByPublic(@PathVariable("id") Integer id, HttpServletRequest request) {
        EventFullDto resultDto = eventService.getEventByIdByPublic(id);
        statsConnector.sendHitEventWithId(request);
        return eventService.setViewsToEvent(request, resultDto);
    }
}
