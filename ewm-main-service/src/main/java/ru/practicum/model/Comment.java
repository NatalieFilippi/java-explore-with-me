package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
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
    @Column
    private long event_id;
    @Column
    private LocalDateTime created_on;
    @Column(name = "user_id")
    @ElementCollection
    @CollectionTable(name="ratings", joinColumns=@JoinColumn(name="comment_id"))
    private Set<Long> rating;
}
