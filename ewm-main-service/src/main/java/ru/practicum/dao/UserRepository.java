package ru.practicum.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.User;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAll(Pageable pageable);

    Page<User> findAllByIdIn(Set<Long> ids, Pageable pageable);
}
