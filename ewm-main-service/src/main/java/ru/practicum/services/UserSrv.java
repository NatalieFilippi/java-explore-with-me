package ru.practicum.services;

import ru.practicum.dto.NewUserDto;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;

import java.util.List;
import java.util.Set;


public interface UserSrv {
    User findById(long userId);
    UserDto createUser(NewUserDto userDto);

    List<UserDto> findAllUsers(Set<Long> ids, int from, int size);

    void deleteUser(long id);
}
