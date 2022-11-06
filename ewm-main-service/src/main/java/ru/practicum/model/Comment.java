package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "COMMENTS")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private long id;
    @Column
    private String text;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @Column(name = "event_id")
    private long eventId;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "user_id")
    @ElementCollection
    @CollectionTable(name = "ratings", joinColumns = @JoinColumn(name = "comment_id"))
    private Set<Long> rating;
}
