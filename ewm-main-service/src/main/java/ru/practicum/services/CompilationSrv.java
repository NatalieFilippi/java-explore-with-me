package ru.practicum.services;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;

import java.util.List;

public interface CompilationSrv {
    CompilationDto getCompilation(long compId);

    List<CompilationDto> getCompilations(int from, int size);

    CompilationDto createCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(long compId);

    void deleteEventFromCollection(long compId, long eventId);

    void addEventToCompilation(long compId, long eventId);

    void pinSwitcher(long compId, boolean tag);

}
