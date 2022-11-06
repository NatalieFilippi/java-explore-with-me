package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PARTICIPATION_REQUESTS")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private long id;
    @Column(name = "event_id")
    private long event;
    @Column
    private LocalDateTime created;
    @Column
    private long requester;
    @Enumerated(EnumType.STRING)
    private StateRequest status;

    public enum StateRequest {
        PENDING,
        CANCELED,
        CONFIRMED,
        REJECTED
    }
}
