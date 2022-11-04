package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dao.UserRepository;
import ru.practicum.dto.NewUserDto;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mappers.UserMapper;
import ru.practicum.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService implements UserSrv {

    private final UserRepository userRepository;

    public User findById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " was not found."));
    }

    @Override
    public UserDto createUser(NewUserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAllUsers(Set<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (ids.isEmpty()) {
            return userRepository.findAll(pageable).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        }
        return userRepository.findAllByIdIn(ids, pageable).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

}
