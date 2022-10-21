package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EVENTS")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private long id;
    private String title;
    private String annotation;
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private LocalDateTime eventDate;
    private boolean paid;
    private int views;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private int confirmedRequests;
    private int participantLimit;
    private boolean requestModeration; //Нужна ли пре-модерация заявок на участие
    @Enumerated(EnumType.STRING)
    private State state;

    public enum State {
        PENDING,
        PUBLISHED,
        CANCELED
    }
}
