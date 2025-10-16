package ru.yandex.practicum.compilation.service;

import ru.yandex.practicum.compilation.CompilationDto;
import ru.yandex.practicum.compilation.NewCompilationDto;
import ru.yandex.practicum.compilation.model.NewCompilation;
import java.util.List;

public interface CompilationService {

    CompilationDto  create(NewCompilationDto newCompilationDto);

    CompilationDto update(Long compId, NewCompilationDto newCompilationDto);

    void delete(Long compId);

    List<CompilationDto> getAll(NewCompilation newCompilation);

    CompilationDto getById(Long compId);
  }