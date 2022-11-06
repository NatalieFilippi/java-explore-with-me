package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> getParticipationRequestsByEvent(long eventId);

    List<ParticipationRequest> findAllByRequester(long userId);

    Integer countByEventAndStatusEquals(long eventId, ParticipationRequest.StateRequest stateRequest);
}
