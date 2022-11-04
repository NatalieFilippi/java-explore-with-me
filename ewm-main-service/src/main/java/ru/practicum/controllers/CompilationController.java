package ru.practicum.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.services.CompilationSrv;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationSrv service;

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable long compId) {
        log.info("Get compilation with id={}", compId);
        return service.getCompilation(compId);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                                @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Get compilations with from={}, size={}", from, size);
        return service.getCompilations(from, size);
    }

    /*
    Admin
     */

    @PostMapping("/admin/compilations") //добавить подборку
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Create compilation {}", compilationDto);
        return service.createCompilation(compilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}") //удалить подборку
    public void deleteCompilation(@PathVariable long compId) {
        log.info("Delete compilation with id={}", compId);
        service.deleteCompilation(compId);
    }

    @DeleteMapping("/admin/compilations/{compId}/events/{eventId}") //удалить событие из подборки
    public void deleteEventFromCollection(@PathVariable long compId,
                                          @PathVariable long eventId) {
        log.info("Delete event {} from compilation with id={}", eventId, compId);
        service.deleteEventFromCollection(compId, eventId);
    }

    @PatchMapping("/admin/compilations/{compId}/events/{eventId}") //добавить событие в подборку
    public void addEventToCompilation(@PathVariable long compId,
                                      @PathVariable long eventId) {
        log.info("Add event {} in compilation with id={}", eventId, compId);
        service.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/admin/compilations/{compId}/pin") //открепить подборку
    public void unpinCompilation(@PathVariable long compId) {
        log.info("Unpin compilation with id={}", compId);
        service.pinSwitcher(compId, false);
    }

    @PatchMapping("/admin/compilations/{compId}/pin") //закрепить подборку
    public void pinCompilation(@PathVariable long compId) {
        log.info("Pin compilation with id={}", compId);
        service.pinSwitcher(compId, true);
    }

}
