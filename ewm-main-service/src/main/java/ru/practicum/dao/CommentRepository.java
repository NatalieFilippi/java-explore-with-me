package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
