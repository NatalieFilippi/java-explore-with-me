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
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository repository;
    private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            .optionalStart()
            .appendFraction(ChronoField.MICRO_OF_SECOND, 1, 9, true)
            .optionalEnd()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            .toFormatter();

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
        end = URLDecoder.decode(end, StandardCharsets.UTF_8);
        LocalDateTime startTime = LocalDateTime.parse(start, dateTimeFormatter);
        LocalDateTime endTime = LocalDateTime.parse(end, dateTimeFormatter);

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
