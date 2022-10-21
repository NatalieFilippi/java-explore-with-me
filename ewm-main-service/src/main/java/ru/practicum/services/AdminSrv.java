package ru.practicum.services;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.dto.NewUserDto;
import ru.practicum.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface AdminSrv {

    /*
    Работа с пользователями
     */
    UserDto createUser(NewUserDto userDto);
    List<UserDto> findAll(Set<Long> ids, int from, int size);
    void deleteUser(long id);


    /*
    Работа с категориями
     */

    CategoryDto createCategory(NewCategoryDto categoryDto);
    void deleteCategory(long categoryId);
    CategoryDto updateCategory(CategoryDto categoryDto);
}
