package ru.practicum.services;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dao.CategoryRepository;
import ru.practicum.dao.UserRepository;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.dto.NewUserDto;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.ApiError;
import ru.practicum.exception.ConflictException;
import ru.practicum.mappers.CategoryMapper;
import ru.practicum.mappers.UserMapper;
import ru.practicum.model.Category;
import ru.practicum.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService implements AdminSrv {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public AdminService(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public UserDto createUser(NewUserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAll(Set<Long> ids, int from, int size) {
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

    @Override
    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        try {
            Category category = categoryRepository.save(CategoryMapper.toCategory(categoryDto));
            return CategoryMapper.toCategoryDto(category);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("could not execute statement; SQL [n/a]; constraint " + categoryDto.getName()
                    + "; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement");
        }
    }

    @Override
    public void deleteCategory(long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        try {
            Category category = categoryRepository.save(CategoryMapper.toCategory(categoryDto));
            return CategoryMapper.toCategoryDto(category);
        } catch (Exception ex) {
            throw new ConflictException("could not execute statement; SQL [n/a]; constraint " + categoryDto.getName()
                    + "; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement");
        }
    }
}
