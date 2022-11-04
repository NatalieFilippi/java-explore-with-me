package ru.practicum.services;

import ru.practicum.dto.*;
import ru.practicum.model.Event;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface EventSrv {
    EventFullDto findById(long eventId);

    List<EventShortDto> findAll(String text, List<Long> categories, String paid, String rangeEnd, String rangeStart, boolean onlyAvailable, int size, int from, String sort) throws UnsupportedEncodingException;

    List<EventShortDto> getUserEvents(long userId, int from, int size);

    List<EventFullDto> findAllEventsForAdmin(List<Long> users, List<Long> categories, List<Event.State> states, String rangeStart, String rangeEnd, int from, int size);

    EventFullDto addEvent(long userId, NewEventDto eventDto);

    EventFullDto updateEvent(long userId, EventUpdateDto eventDto);

    EventFullDto getFullUserEvents(long userId, long eventId);

    EventFullDto cancelEvent(long userId, long eventId);

    EventFullDto publishEvent(long eventId);

    EventFullDto rejectEvent(long eventId);

    EventFullDto adminUpdateEvent(long eventId, AdminUpdateEventRequest eventDto);
}
