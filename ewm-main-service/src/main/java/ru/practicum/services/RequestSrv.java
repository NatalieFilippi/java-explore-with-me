package ru.practicum.services;

import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestSrv {

    List<ParticipationRequestDto> getRequestsByEvent(long userId, long eventId);

    ParticipationRequestDto confirmRequest(long userId, long eventId, long reqId);

    ParticipationRequestDto rejectRequest(long userId, long eventId, long reqId);

    List<ParticipationRequestDto> findAllByUser(long userId);

    ParticipationRequestDto addParticipationRequest(long userId, long eventId);

    ParticipationRequestDto cancelParticipationRequest(long userId, long requestId);
}
