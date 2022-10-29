package ru.practicum.services;

import org.springframework.stereotype.Service;
import ru.practicum.dao.RequestRepository;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService implements RequestSrv {
    private final RequestRepository requestRepository;
    private final EventSrv eventService;
    private final UserSrv userService;

    public RequestService(RequestRepository requestRepository, EventSrv eventService, UserService userService) {
        this.requestRepository = requestRepository;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(long userId, long eventId) {
        userService.findById(userId);
        eventService.findById(eventId);
        return requestRepository.getParticipationRequestsByEvent(eventId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequest(long userId, long eventId, long reqId) {
        userService.findById(userId);
        EventFullDto event = eventService.findById(eventId);
        ParticipationRequest request = findById(reqId);
        if (event.getConfirmedRequests() != 0 || event.isRequestModeration()) {
            List<ParticipationRequest> requests = requestRepository.getParticipationRequestsByEvent(eventId);
            long count = requests.stream().filter(r -> r.getStatus() == ParticipationRequest.StateRequest.CONFIRMED).count();
            if (count == event.getParticipantLimit()) {
                throw new ValidationException("The limit of participants in the event has been reached");
            }
            if ((event.getParticipantLimit() - count) == 1) {
                requests = requests.stream()
                        .filter(r -> r.getStatus() == ParticipationRequest.StateRequest.PENDING)
                        .collect(Collectors.toList());
                for (ParticipationRequest participationRequest : requests) {
                    participationRequest.setStatus(ParticipationRequest.StateRequest.CANCELED);
                    requestRepository.save(participationRequest);
                }
            }
        }

        request.setStatus(ParticipationRequest.StateRequest.CONFIRMED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto rejectRequest(long userId, long eventId, long reqId) {
        userService.findById(userId);
        eventService.findById(eventId);
        ParticipationRequest request = findById(reqId);
        request.setStatus(ParticipationRequest.StateRequest.REJECTED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> findAllByUser(long userId) {
        return requestRepository.findAllByRequester(userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(long userId, long eventId) {
        userService.findById(userId);
        EventFullDto event = eventService.findById(eventId);
        if (!event.getState().equals(Event.State.PUBLISHED.toString())) {
            throw new ValidationException("The event " + eventId + " has not been published.");
        }
        if (event.getInitiator().getId() == userId) {
            throw new ValidationException("The initiator of the event cannot add a request to participate in his event.");
        }
        List<ParticipationRequest> requests = requestRepository.getParticipationRequestsByEvent(eventId);
        long count = requests.stream().filter(r -> r.getStatus() == ParticipationRequest.StateRequest.CONFIRMED).count();
        if (count == event.getParticipantLimit()) {
            throw new ValidationException("The limit of participants in the event has been reached");
        }
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(userId);
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setEvent(eventId);
        participationRequest.setStatus(event.isRequestModeration() ? ParticipationRequest.StateRequest.PENDING : ParticipationRequest.StateRequest.CONFIRMED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(long userId, long requestId) {
        userService.findById(userId);
        ParticipationRequest request = findById(requestId);
        request.setStatus(ParticipationRequest.StateRequest.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    public ParticipationRequest findById(long reqId) {
        return requestRepository.findById(reqId).orElseThrow(() -> new ObjectNotFoundException("Participation request with id=" + reqId + " was not found."));
    }

}
