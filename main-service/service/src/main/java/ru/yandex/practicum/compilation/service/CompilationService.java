package ru.yandex.practicum.compilation.service;

import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto saveCompilation(NewCompilationDto compilationWriteDto);

    void deleteCompilation(Integer compId);

    CompilationDto updateCompilation(NewCompilationDto compilationWriteDto, Integer compId);

    List<CompilationDto> getCompilationListByPublic(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationByIdByPublic(Integer compId);
  }