package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.EventClient;
import ru.practicum.dto.*;
import ru.practicum.model.Event;
import ru.practicum.services.EventSrv;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class EventController {

    private final EventSrv service;
    private final EventClient client;

    /*
    Public
     */


    @GetMapping("/events")
    public List<EventShortDto> getEvents(HttpServletRequest request,
                                         @RequestParam(required = false, defaultValue = "") String text,
                                         @RequestParam(required = false, defaultValue = "") List<Long> categories,
                                         @RequestParam(required = false, defaultValue = "all") String paid,
                                         @RequestParam(required = false, defaultValue = "") String rangeStart,
                                         @RequestParam(required = false, defaultValue = "") String rangeEnd,
                                         @RequestParam(required = false, defaultValue = "all") boolean onlyAvailable,
                                         @RequestParam(required = false, defaultValue = "false") String sort,
                                         @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                         @Positive @RequestParam(required = false, defaultValue = "10") int size) throws UnsupportedEncodingException {
        sendHit(request);
        log.info("Get events with text {}, categories {},paid {},rangeEnd {},rangeStart {},onlyAvailable {},sort {}, from {},size {},",
                text, categories, paid, rangeEnd, rangeStart, onlyAvailable, sort, from, size);
        return service.findAll(text, categories, paid, rangeEnd, rangeStart, onlyAvailable, size, from, sort);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEvent(@PathVariable long eventId, HttpServletRequest request) {
        sendHit(request);
        log.info("Get event {}", eventId);
        return service.findById(eventId);
    }

    /*
    Private
     */

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getUserEvents(@PathVariable long userId,
                                             @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                             @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Get user {} events", userId);
        return service.getUserEvents(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    public EventFullDto addEvent(@PathVariable long userId,
                                 @Valid @RequestBody NewEventDto eventDto) {
        log.info("New user {} event {}", userId, eventDto);
        return service.addEvent(userId, eventDto);
    }

    @PatchMapping("/users/{userId}/events")
    public EventFullDto updateEvent(@PathVariable long userId,
                                    @Valid @RequestBody EventUpdateDto eventDto) {
        log.info("Edit a user {} event {}", userId, eventDto);
        return service.updateEvent(userId, eventDto);
    }


    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getFullUserEvents(@PathVariable long userId,
                                          @PathVariable long eventId) {
        log.info("Get user {} event {}", userId, eventId);
        return service.getFullUserEvents(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto cancelEvent(@PathVariable long userId,
                                    @PathVariable long eventId) {
        log.info("Edit a user {} event {}", userId, eventId);
        return service.cancelEvent(userId, eventId);
    }

    private void sendHit(HttpServletRequest request) {
        HitDto hit = new HitDto();
        hit.setApp("ewm-main-service");
        hit.setIp(request.getRemoteAddr());
        hit.setUri(request.getRequestURI());
        hit.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.info("Sending hit {}", hit);
        client.send(hit);
    }


    /*
    Admin
     */

    @GetMapping("/admin/events")
    public List<EventFullDto> findAllEventsForAdmin(@RequestParam(required = false, defaultValue = "") List<Long> users,
                                                    @RequestParam(required = false, defaultValue = "") List<Event.State> states,
                                                    @RequestParam(required = false, defaultValue = "") List<Long> categories,
                                                    @RequestParam(required = false, defaultValue = "") String rangeStart,
                                                    @RequestParam(required = false, defaultValue = "") String rangeEnd,
                                                    @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                                    @Positive @RequestParam(required = false, defaultValue = "10") int size
    ) {
        log.info("Get events with users {}, states {}, categories {}, rangeStart {}, rangeEnd {}, from {}, size {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return service.findAllEventsForAdmin(users, categories, states, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable long eventId) {
        log.info("Publish event with id={}", eventId);
        return service.publishEvent(eventId);
    }

    @PatchMapping("/admin/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable long eventId) {
        log.info("Reject event with id={}", eventId);
        return service.rejectEvent(eventId);
    }

    @PutMapping("/admin/events/{eventId}")
    public EventFullDto adminUpdateEvent(@PathVariable long eventId,
                                         @RequestBody AdminUpdateEventRequest eventDto) {
        log.info("Admin update event with id={}", eventId);
        return service.adminUpdateEvent(eventId, eventDto);
    }
}
