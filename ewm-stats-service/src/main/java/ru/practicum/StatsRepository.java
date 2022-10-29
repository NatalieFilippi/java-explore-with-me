package ru.practicum;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    List<ViewStats> findAll(Specification<Hit> hitSpecification);

    @Query("select h.app, h.uri, count(h.ip) as hits " +
            "from Hit h where h.time between ?1 and ?2 " +
            "group by h.app, h.uri")
    List<ViewStats> getAll(LocalDateTime start, LocalDateTime end);
}
