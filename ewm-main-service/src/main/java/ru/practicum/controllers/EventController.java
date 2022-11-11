package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.EventClient;
import ru.practicum.dto.*;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.services.EventSrv;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
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
                                         @RequestParam(required = false) boolean paid,
                                         @RequestParam(required = false, defaultValue = "") String rangeStart,
                                         @RequestParam(required = false, defaultValue = "") String rangeEnd,
                                         @RequestParam(required = false) boolean onlyAvailable,
                                         @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
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

    /*
    Комментарии
     */

    @PostMapping("/users/{userId}/events/{eventId}/comments")
    public CommentDto addComment(@PathVariable long userId,
                                 @PathVariable long eventId,
                                 @Valid @RequestBody NewCommentDto commentDto) {
        log.info("User with id = {} added a comment {} to the event {}", userId, commentDto, eventId);
        return service.addComment(userId, eventId, commentDto);
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/comments/{comId}")
    public void deleteComment(@PathVariable long userId,
                              @PathVariable long eventId,
                              @PathVariable long comId) {
        log.info("User with id = {} deleted a comment {} to the event {}", userId, comId, eventId);
        service.deleteComment(userId, eventId, comId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/comments/{comId}")
    public CommentDto updateComment(@PathVariable long userId,
                                    @PathVariable long eventId,
                                    @PathVariable long comId,
                                    @Size(min = 2) @Size(max = 2000) @RequestParam String text) {
        log.info("User with id = {} updated a comment {} to the event {}", userId, comId, eventId);
        return service.updateComment(userId, eventId, comId, text);
    }

    @PostMapping("/users/{userId}/events/{eventId}/comments/{comId}/like")
    public void likeComment(@PathVariable long userId,
                            @PathVariable long eventId,
                            @PathVariable long comId) {
        log.info("User with id = {} liked the comment {} to the event {}", userId, comId, eventId);
        service.likeComment(userId, eventId, comId);
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/comments/{comId}/like")
    public void removeLike(@PathVariable long userId,
                           @PathVariable long eventId,
                           @PathVariable long comId) {
        log.info("User with id = {} removed like the comment {} to the event {}", userId, comId, eventId);
        service.removeLike(userId, eventId, comId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/comments")
    public List<CommentDto> getComments(@PathVariable long userId,
                                        @PathVariable long eventId,
                                        @RequestParam(defaultValue = "RATING") Comment.SortComment sort, //{VALUE=OLD_DATE, NEW_DATE, RATING}
                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                        @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get event {} comments with sort = {}, from = {}, size = {} by user {}", eventId, sort, from, size, userId);
        return service.getComments(userId, eventId, sort, from, size);
    }

}
