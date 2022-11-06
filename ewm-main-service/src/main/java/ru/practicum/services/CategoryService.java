package ru.practicum.services;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dao.CategoryRepository;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mappers.CategoryMapper;
import ru.practicum.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategorySrv {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
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
    public CategoryDto getCategory(long catId) {
        return CategoryMapper.toCategoryDto(categoryRepository
                .findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + catId + " was not found.")));
    }

    @Override
    public void deleteCategory(long categoryId) {
        categoryRepository.findById(categoryId).orElseThrow(() -> new ObjectNotFoundException("Event with id=" + categoryId + " was not found."));
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
