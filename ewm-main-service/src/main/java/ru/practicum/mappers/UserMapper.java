package ru.practicum.mappers;

import ru.practicum.dto.NewUserDto;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;

public class UserMapper {

    public static User toUser(NewUserDto userDto) {
        if (userDto == null) {
            return null;
        }
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        return user;
    }

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setId(user.getId());
        return userDto;
    }
}
