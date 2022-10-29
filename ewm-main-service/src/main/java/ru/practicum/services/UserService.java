package ru.practicum.services;

import org.springframework.stereotype.Service;
import ru.practicum.dao.UserRepository;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.model.User;


@Service
public class UserService implements UserSrv {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " was not found."));
    }

}
