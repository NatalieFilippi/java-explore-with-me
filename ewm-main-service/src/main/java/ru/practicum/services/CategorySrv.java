package ru.practicum.services;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

public interface CategorySrv {
    List<CategoryDto> getCategories(int from, int size);

    CategoryDto createCategory(NewCategoryDto categoryDto);

    void deleteCategory(long categoryId);

    CategoryDto updateCategory(CategoryDto categoryDto);

    CategoryDto getCategory(long catId);

}
