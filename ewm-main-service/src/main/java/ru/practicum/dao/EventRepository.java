package ru.practicum.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.util.Set;


public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Page<Event> findAllByInitiator(User user, Pageable pageable);

    @Query(value = "select *" +
            " from events e left join" +
            " (select p.event_id, count(p.request_id) from PARTICIPATION_REQUESTS p" +
            " where p.status like 'CONFIRMED' group by p.event_id) i" +
            " on e.event_id = i.event_id" +
            " where e.participant_limit > coalesce(i.count, 0);", nativeQuery = true)
    Set<Event> getAvailableEvents();
}
