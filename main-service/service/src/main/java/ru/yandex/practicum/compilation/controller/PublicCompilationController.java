package ru.yandex.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.compilation.model.NewCompilation;
import ru.yandex.practicum.compilation.CompilationDto;
import ru.yandex.practicum.compilation.service.CompilationService;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@AllArgsConstructor
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned, @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        NewCompilation newCompilation = new NewCompilation(pinned, from, size);
        return  compilationService.getAll(newCompilation);
    }

    @GetMapping("{compId}")
    public CompilationDto getById(@PathVariable Long compId) {
        return  compilationService.getById(compId);
    }
}
