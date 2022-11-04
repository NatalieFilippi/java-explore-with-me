package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.NewUserDto;
import ru.practicum.dto.UserDto;
import ru.practicum.services.UserSrv;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserSrv service;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserDto userDto) {
        log.info("Create user {}", userDto);
        return service.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> findAll(@RequestParam(required = false, defaultValue = "") Set<Long> ids,
                                 @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                 @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Get events with ids {}, from {}, size {}", ids, from, size);
        return service.findAllUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Delete user with id={}", userId);
        service.deleteUser(userId);
    }
}
