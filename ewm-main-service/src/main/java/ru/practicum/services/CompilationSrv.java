package ru.practicum.services;

import ru.practicum.dto.CompilationDto;

import java.util.List;

public interface CompilationSrv {
    CompilationDto getCompilation(long compId);

    List<CompilationDto> getCompilations(int from, int size);
}
