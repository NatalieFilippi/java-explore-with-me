package ru.practicum;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsService {

    private final StatsRepository repository;
    private final EntityManager em;

    public StatsService(StatsRepository repository, EntityManager em) {
        this.repository = repository;
        this.em = em;
    }

    public void addHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setIp(hitDto.getIp());
        hit.setUri(hitDto.getUri());
        hit.setTime(LocalDateTime.now());
        repository.save(hit);
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) throws UnsupportedEncodingException {

        start = URLDecoder.decode(start, "UTF-8");
        end = URLDecoder.decode(end, "UTF-8");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime;
        LocalDateTime endTime;
        try {
            startTime = LocalDateTime.parse(start);
            endTime = LocalDateTime.parse(end);
        } catch (Exception ex) {
            startTime = LocalDateTime.parse(start, formatter);
            endTime = LocalDateTime.parse(end, formatter);
        }

        StringBuilder q = new StringBuilder();
        q.append("select NEW ru.practicum.ViewStats(app, uri, count(");
        if (unique) {
            q.append("distinct ");
        }
        q.append("ip)) from Hit where time between :start and :end ");
        if (uris != null && !uris.isEmpty()) {
            q.append("and uri in :uris ");
        }
        q.append("group by app, uri");

        TypedQuery<ViewStats> query = em.createQuery(q.toString(), ViewStats.class);
        if (uris != null && !uris.isEmpty()) {
            return query
                    .setParameter("start", startTime)
                    .setParameter("end", endTime)
                    .setParameter("uris", uris)
                    .getResultList();
        }
        return query
                .setParameter("start", startTime)
                .setParameter("end", endTime)
                .getResultList();
    }
}
