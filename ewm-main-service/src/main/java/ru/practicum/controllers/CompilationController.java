package ru.practicum.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.services.CompilationSrv;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/compilations")
public class CompilationController {
    private final CompilationSrv service;

    public CompilationController(CompilationSrv service) {
        this.service = service;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable long compId) {
        log.info("Get compilation with id={}", compId);
        return service.getCompilation(compId);
    }

    @GetMapping
    public List<CompilationDto> getCompilations(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                                @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Get compilations with from={}, size={}", from, size);
        return service.getCompilations(from, size);
    }
}
