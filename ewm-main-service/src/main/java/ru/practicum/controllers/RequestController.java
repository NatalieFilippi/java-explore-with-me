package ru.practicum.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.services.RequestSrv;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users/{userId}")
public class RequestController {

    private final RequestSrv service;

    public RequestController(RequestSrv service) {
        this.service = service;
    }

    @GetMapping("/events/{eventId}/requests")
    //Получение информации о запросах на участие в событии текущего пользователя
    public List<ParticipationRequestDto> getRequestsByEvent(@PathVariable long userId,
                                                            @PathVariable long eventId) {
        log.info("Get requests by event {} from user {}", eventId, userId);
        return service.getRequestsByEvent(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/confirm")
    //Подтверждение чужой заявки на участие в событии текущего пользователя
    public ParticipationRequestDto confirmRequest(@PathVariable long userId,
                                                  @PathVariable long eventId,
                                                  @PathVariable long reqId) {
        log.info("Confirm requests {} for participation in the event {} of the current user {}", reqId, eventId, userId);
        return service.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/reject")
    //Отклонение чужой заявки на участие в событии текущего пользователя
    public ParticipationRequestDto rejectRequest(@PathVariable long userId,
                                                 @PathVariable long eventId,
                                                 @PathVariable long reqId) {
        log.info("Reject requests {} for participation in the event {} of the current user {}", reqId, eventId, userId);
        return service.rejectRequest(userId, eventId, reqId);
    }

    @GetMapping("/requests")
    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    public List<ParticipationRequestDto> findAllByUser(@PathVariable long userId) {
        log.info("Getting information about the user's {} requests to participate in other people's events", userId);
        return service.findAllByUser(userId);
    }

    @PostMapping("/requests")
    //Добавление запроса от текущего пользователя на участие в событии
    public ParticipationRequestDto addParticipationRequest(@PathVariable long userId,
                                                           @RequestParam long eventId) {
        log.info("Adding a request from the current user {} to participate in the event {}", userId, eventId);
        return service.addParticipationRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    //Отмена своего запроса на участие в событии
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable long userId,
                                                              @PathVariable long requestId) {
        log.info("Cancellation of your request {} to participate in the event {}", requestId, userId);
        return service.cancelParticipationRequest(userId, requestId);
    }
}
