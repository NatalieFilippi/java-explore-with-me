package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dao.StatsRepository;
import ru.practicum.dto.ViewStats;
import ru.practicum.dto.HitDto;
import ru.practicum.model.Hit;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository repository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void addHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setIp(hitDto.getIp());
        hit.setUri(hitDto.getUri());
        hit.setTime(LocalDateTime.now());
        repository.save(hit);
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {

        start = URLDecoder.decode(start, StandardCharsets.UTF_8);
        start = start.replace("T", " ").substring(0, !start.contains(".") ? start.length() : start.indexOf("."));
        end = URLDecoder.decode(end, StandardCharsets.UTF_8);
        end = end.replace("T", " ").substring(0, !end.contains(".") ? end.length() : end.indexOf("."));
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);

        if (unique) {
            if (uris != null && !uris.isEmpty()) {
                return repository.getAllWithUniqueIpAndUris(startTime, endTime, uris);
            }
            return repository.getAllWithUniqueIp(startTime, endTime);
        }
        if (uris != null && !uris.isEmpty()) {
            return repository.getAllWithUris(startTime, endTime, uris);
        }
        return repository.getAll(startTime, endTime);
    }
}
