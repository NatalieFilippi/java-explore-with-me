package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.EventClient;
import ru.practicum.dao.CategoryRepository;
import ru.practicum.dao.CommentRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dto.*;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService implements EventSrv {
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final UserSrv userService;
    private final CategoryRepository categoryRepository;
    private final EventClient client;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EntityManager em;

    @Override
    public EventFullDto findById(long eventId) {
        return EventMapper.toEventFullDto(eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " was not found.")));
    }

    private Event getEvent(long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " was not found."));
    }

    private Category getCategory(long catId) {
        return categoryRepository
                .findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + catId + " was not found."));
    }

    @Override
    public List<EventShortDto> findAll(String text,
                                       List<Long> categories,
                                       boolean paid,
                                       String rangeEnd,
                                       String rangeStart,
                                       boolean onlyAvailable,
                                       int size,
                                       int from,
                                       String sort) {
        Page<Event> events = findAllForUser(text, categories, paid, rangeEnd, rangeStart, size, from, sort);
        if (onlyAvailable) {
            Set<Event> availableEvents = eventRepository.getAvailableEvents();
            return events.stream()
                    .filter(availableEvents::contains)
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        }

        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    private Page<Event> findAllForUser(String text,
                                       List<Long> categories,
                                       boolean paid,
                                       String rangeEnd,
                                       String rangeStart,
                                       int size,
                                       int from,
                                       String sort
    ) {
        Specification<Event> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            query.orderBy(criteriaBuilder.desc(root.get("eventDate")));

            if (!text.isBlank()) {
                Predicate predicateAnnotation = criteriaBuilder.like(root.get("annotation"), "%" + text + "%");
                Predicate predicateDescription = criteriaBuilder.like(root.get("description"), "%" + text + "%");
                predicates.add(criteriaBuilder.or(predicateAnnotation, predicateDescription));
            }

            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("paid"), paid)));

            if (categories != null && !categories.isEmpty()) {
                for (Long categoriesId : categories) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("category"), categoriesId)));
                }
            }

            if (!rangeStart.isBlank()) {
                try {
                    LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
                    Predicate onStart = criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), start);
                    predicates.add(onStart);
                } catch (DateTimeException ex) {
                    throw new ValidationException("The rangeStart must have a date in the format \"yyyy-MM-dd HH:mm:ss\"");
                }
            }

            if (!rangeEnd.isBlank()) {
                try {
                    LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);
                    Predicate onEnd = criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end);
                    predicates.add(onEnd);
                } catch (DateTimeException ex) {
                    throw new ValidationException("The rangeEnd must have a date in the format \"yyyy-MM-dd HH:mm:ss\"");
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        List<Event> events = eventRepository.findAll(specification);
        for (Event event : events) {
            event.setViews(getViews(event.getId()));
        }
        Pageable pageable = PageRequest.of(from / size, size);

        if (sort.equals("VIEWS")) {
            pageable = PageRequest.of(from / size, size, Sort.by("views"));
        }
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());
        return eventPage;
    }

    @Override
    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        User user = userService.findById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiator(user, pageable)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(long userId, NewEventDto eventDto) {
        User user = userService.findById(userId);
        Category category = getCategory(eventDto.getCategory());

        Event event = EventMapper.toEvent(eventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(Event.State.PENDING);
        eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(long userId, EventUpdateDto eventDto) {
        userService.findById(userId);
        Event event = getEvent(eventDto.getId());
        if (event.getState() == Event.State.PUBLISHED) {
            throw new ValidationException("Only pending or canceled events can be changed");
        }
        Event updateEvent = EventMapper.toEvent(eventDto);
        if (updateEvent.getEventDate().plusHours(2).isBefore(LocalDateTime.now())) {
            throw new ValidationException("The date and time for which the event is scheduled cannot be earlier than two hours from the current moment");
        }
        Category category = getCategory(eventDto.getCategory());

        event.setState(Event.State.PENDING);
        event.setDescription(updateEvent.getDescription());
        event.setAnnotation(updateEvent.getAnnotation());
        event.setCategory(category);
        event.setPaid(updateEvent.isPaid());
        event.setParticipantLimit(updateEvent.getParticipantLimit());
        event.setEventDate(updateEvent.getEventDate());
        event.setTitle(updateEvent.getTitle());
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getFullUserEvents(long userId, long eventId) {
        User user = userService.findById(userId);
        Event event = getEvent(eventId);
        if (event.getInitiator() != user) {
            throw new ValidationException("The user is not the author of the event");
        }
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto cancelEvent(long userId, long eventId) {
        userService.findById(userId);
        Event event = getEvent(eventId);
        if (event.getState() == Event.State.PUBLISHED) {
            throw new ValidationException("You can only cancel an event in the publication waiting state.");
        }
        event.setState(Event.State.CANCELED);
        eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto publishEvent(long eventId) {
        Event event = getEvent(eventId);
        if (event.getState() != Event.State.PENDING) {
            throw new ValidationException("The event must be in the publication waiting state");
        }
        if (event.getEventDate().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new ValidationException("The start date of the event must be no earlier than an hour from the date of publication.");
        }
        event.setState(Event.State.PUBLISHED);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(long eventId) {
        Event event = getEvent(eventId);
        if (event.getState() == Event.State.PUBLISHED) {
            throw new ValidationException("A published event cannot be rejected.");
        }
        event.setState(Event.State.CANCELED);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto adminUpdateEvent(long eventId, AdminUpdateEventRequest eventDto) {
        Event event = getEvent(eventId);
        if (eventDto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventDto.getEventDate(), formatter));
        }
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getLocation() != null) {
            event.setLocation(eventDto.getLocation());
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getParticipantLimit() != 0) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getCategory() != 0) {
            event.setCategory(getCategory(eventDto.getCategory()));
        }
        event.setPaid(eventDto.isPaid());
        event.setRequestModeration(eventDto.isRequestModeration());
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public CommentDto addComment(long userId, long eventId, NewCommentDto commentDto) {
        User user = userService.findById(userId);
        getEvent(eventId);
        Comment newComment = new Comment();
        newComment.setText(commentDto.getText());
        newComment.setEventId(eventId);
        newComment.setAuthor(user);
        newComment.setCreatedOn(LocalDateTime.now());
        newComment.setRating(new HashSet<>());
        commentRepository.save(newComment);
        return EventMapper.toCommentDto(newComment);
    }

    public Comment findCommentById(long comId) {
        return commentRepository
                .findById(comId)
                .orElseThrow(() -> new ObjectNotFoundException("Comment with id=" + comId + " was not found."));
    }

    @Override
    public void likeComment(long userId, long eventId, long comId) {
        Comment comment = findCommentById(comId);
        Set<Long> rating = comment.getRating();
        if (rating.contains(userId)) {
            throw new ValidationException("The user has already liked the comment");
        } else {
            rating.add(userId);
            comment.setRating(rating);
            commentRepository.save(comment);
        }
    }

    @Override
    public void removeLike(long userId, long eventId, long comId) {
        Comment comment = findCommentById(comId);
        Set<Long> rating = comment.getRating();
        if (rating.contains(userId)) {
            rating.remove(userId);
            comment.setRating(rating);
            commentRepository.save(comment);
        } else {
            throw new ValidationException("The user " + userId + " did not like the comment " + comId);
        }
    }

    @Override
    public List<CommentDto> getComments(long userId, long eventId, String sort, int from, int size) {
        StringBuilder q = new StringBuilder();
        q.append("select c from Comment c where c.eventId =:eventId order by ");
        switch (sort) {
            case "RATING":
                q.append("c.rating.size desc");
                break;
            case "NEW_DATE":
                q.append("c.createdOn asc");
                break;
            case "OLD_DATE":
                q.append("c.createdOn desc");
        }

        TypedQuery<Comment> query = em.createQuery(q.toString(), Comment.class);
        List<Comment> comments = query.setParameter("eventId", eventId)
                .setMaxResults(size)
                .setFirstResult(from)
                .getResultList();
        return comments.stream().map(EventMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public void deleteComment(long userId, long eventId, long comId) {
        findCommentById(comId);
        commentRepository.deleteById(comId);
    }

    @Override
    public CommentDto updateComment(long userId, long eventId, long comId, String text) {
        User user = userService.findById(userId);
        Comment comment = findCommentById(comId);
        if (comment.getAuthor() == user) {
            comment.setText(text);
            commentRepository.save(comment);
            return EventMapper.toCommentDto(comment);
        } else {
            throw new ValidationException("Only the author of the comment can change the comment");
        }
    }

    @Override
    public List<EventFullDto> findAllEventsForAdmin(List<Long> usersIds,
                                                    List<Long> categoriesIds,
                                                    List<Event.State> states,
                                                    String rangeStart,
                                                    String rangeEnd,
                                                    int from,
                                                    int size) {
        Page<Event> events = findAllForAdmin(usersIds, categoriesIds, states, rangeStart, rangeEnd, from, size);

        return events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    private Page<Event> findAllForAdmin(List<Long> usersIds,
                                        List<Long> categoriesIds,
                                        List<Event.State> states,
                                        String rangeStart,
                                        String rangeEnd,
                                        int from,
                                        int size
    ) {
        Pageable pageable = PageRequest.of(from / size, size);
        Specification<Event> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (usersIds != null && !usersIds.isEmpty()) {
                for (Long usersId : usersIds) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("initiator"), usersId)));
                }
            }

            if (categoriesIds != null && !categoriesIds.isEmpty()) {
                for (Long categoriesId : categoriesIds) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("category"), categoriesId)));
                }
            }

            if (states != null) {
                for (Event.State state : states) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("state"), state)));
                }
            }

            if (!rangeStart.isBlank()) {
                try {
                    LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
                    Predicate onStart = criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), start);
                    predicates.add(onStart);
                } catch (DateTimeException ex) {
                    throw new ValidationException("The rangeStart must have a date in the format \"yyyy-MM-dd HH:mm:ss\"");
                }
            }

            if (!rangeEnd.isBlank()) {
                try {
                    LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);
                    Predicate onEnd = criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end);
                    predicates.add(onEnd);
                } catch (DateTimeException ex) {
                    throw new ValidationException("The rangeEnd must have a date in the format \"yyyy-MM-dd HH:mm:ss\"");
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return eventRepository.findAll(specification, pageable);
    }

    private Long getViews(Long id) {
        String uri = "/events/";
        String start = URLEncoder.encode(LocalDateTime.now().minusYears(1).toString(), StandardCharsets.UTF_8);
        String end = URLEncoder.encode(LocalDateTime.now().toString(), StandardCharsets.UTF_8);
        String parameterId = uri + id;

        ResponseEntity<Object> views = client.getHits(start, end, parameterId, "false");
        List<ViewStats> v = (List<ViewStats>) views.getBody();
        return v.size() == 0 ? 0 : v.get(0).getHits();
    }
}
