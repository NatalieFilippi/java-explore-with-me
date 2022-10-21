package ru.practicum.controllers;

import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.dto.NewUserDto;
import ru.practicum.dto.UserDto;
import ru.practicum.services.AdminSrv;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/admin")
public class AdminController {
    private final AdminSrv service;

    public AdminController(AdminSrv service) {
        this.service = service;
    }

    /*
    API для работы с пользователями
     */

    @PostMapping("/users")
    public UserDto createUser(@Valid @RequestBody NewUserDto userDto) {
        return service.createUser(userDto);
    }

    @GetMapping("/users")
    public List<UserDto> findAll(@RequestParam(required = false, defaultValue = "") Set<Long> ids,
                                 @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                 @Positive @RequestParam(required = false, defaultValue = "20") int size) {
        return service.findAll(ids, from, size);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable long userId) {
        service.deleteUser(userId);
    }


    /*
    API для работы с категориями
     */

    @PostMapping("/categories")
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto categoryDto) {
        return service.createCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@PathVariable long catId) {
        service.deleteUser(catId);
    }

    @PatchMapping("/categories")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return service.updateCategory(categoryDto);
    }
}
