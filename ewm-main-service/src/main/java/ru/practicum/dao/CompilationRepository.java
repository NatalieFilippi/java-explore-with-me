package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.Compilation;

import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query(value = "select event_id from compilation_events where compilation_id = :copmId and event_id = :eventId", nativeQuery = true)
    Optional<Long> findEventFromCompilation(long copmId, long eventId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from compilation_events where compilation_id = :copmId and event_id = :eventId", nativeQuery = true)
    void deleteEventFromCompilation(long copmId, long eventId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "insert into compilation_events(compilation_id, event_id) values (:copmId, :eventId)", nativeQuery = true)
    void addEventToCompilation(long copmId, long eventId);
}
