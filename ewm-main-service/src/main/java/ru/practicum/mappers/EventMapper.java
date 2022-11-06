package ru.practicum.mappers;

import ru.practicum.dto.*;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventFullDto toEventFullDto(Event event) {
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

    public static CommentDto toCommentDto(Comment comment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        CommentDto commentDto = new CommentDto();
        commentDto.setEventId(comment.getEventId());
        commentDto.setDate(comment.getCreatedOn().format(formatter));
        commentDto.setText(comment.getText());
        commentDto.setAuthor(UserMapper.toUserShort(comment.getAuthor()));
        commentDto.setRating(comment.getRating() == null ? 0 : comment.getRating().size());
        commentDto.setId(comment.getId());
        return commentDto;
    }
}
