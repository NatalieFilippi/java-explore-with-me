package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("select NEW ru.practicum.dto.ViewStats(app, uri, count(ip)) " +
            "from Hit where time between :start and :end group by app, uri")
    List<ViewStats> getAll(LocalDateTime start, LocalDateTime end);

    @Query("select NEW ru.practicum.dto.ViewStats(app, uri, count(distinct ip)) " +
            "from Hit where time between :start and :end group by app, uri")
    List<ViewStats> getAllWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select NEW ru.practicum.dto.ViewStats(app, uri, count(distinct ip)) " +
            "from Hit where time between :start and :end and uri in :uris group by app, uri")
    List<ViewStats> getAllWithUniqueIpAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select NEW ru.practicum.dto.ViewStats(app, uri, count(ip)) " +
            "from Hit where time between :start and :end and uri in :uris group by app, uri")
    List<ViewStats> getAllWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
