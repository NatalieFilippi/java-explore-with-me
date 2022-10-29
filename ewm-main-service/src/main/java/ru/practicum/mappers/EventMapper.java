package ru.practicum.mappers;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.EventUpdateDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate().format(formatter))
                .category(event.getCategory())
                .paid(event.isPaid())
                .createdOn(event.getCreatedOn().format(formatter))
                .description(event.getDescription())
                .title(event.getTitle())
                .state(event.getState().toString())
                .confirmedRequests(event.getConfirmedRequests())
                .initiator(UserMapper.toUserShort(event.getInitiator()))
                .location(event.getLocation())
                .participantLimit(event.getParticipantLimit())
                .views(event.getViews())
                .publishedOn(event.getPublishedOn() == null ? "" : event.getPublishedOn().format(formatter))
                .requestModeration(event.isRequestModeration())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return EventShortDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate().format(formatter))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .category(event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .annotation(event.getAnnotation())
                .initiator(UserMapper.toUserShort(event.getInitiator()))
                .build();
    }

    public static Event toEvent(NewEventDto eventDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Event.builder()
                .eventDate(eventDto.getEventDate() == null ? null : LocalDateTime.parse(eventDto.getEventDate(), formatter))
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .location(LocationMapper.toLocation(eventDto.getLocation()))
                .paid(eventDto.isPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.isRequestModeration())
                .title(eventDto.getTitle())
                .build();
    }

    public static Event toEvent(EventUpdateDto eventDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Event.builder()
                .eventDate(eventDto.getEventDate() == null ? null : LocalDateTime.parse(eventDto.getEventDate(), formatter))
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .id(eventDto.getId())
                .paid(eventDto.isPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .title(eventDto.getTitle())
                .build();
    }
}
