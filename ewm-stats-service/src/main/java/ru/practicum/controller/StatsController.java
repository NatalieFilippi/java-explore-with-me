package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ViewStats;
import ru.practicum.dto.HitDto;
import ru.practicum.service.StatsService;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    public void addHit(@RequestBody HitDto hit) {
        log.info("Add hit {}", hit);
        service.addHit(hit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam(defaultValue = "") String start,
                                    @RequestParam(defaultValue = "") String end,
                                    @RequestParam(required = false, defaultValue = "") List<String> uris,
                                    @RequestParam(required = false, defaultValue = "false") boolean unique) throws UnsupportedEncodingException {
        log.info("Get stats with start {}, end {}, uris {}, unique {}", start, end, uris, unique);
        return service.getStats(start, end, uris, unique);
    }
}
