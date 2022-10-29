package ru.practicum.services;

import ru.practicum.model.User;


public interface UserSrv {
    public User findById(long userId);
}
