package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.services.CategorySrv;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CategoryController {
    private final CategorySrv categoryService;

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable long catId) {
        log.info("Get category with id={}", catId);
        return categoryService.getCategory(catId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                           @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Get categories with from={}, size={}", from, size);
        return categoryService.getCategories(from, size);
    }


    /*
    Admin
     */

    @PostMapping("/admin/categories")
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto categoryDto) {
        log.info("Create category {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public void deleteCategory(@PathVariable long catId) {
        log.info("Delete category with id={}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/admin/categories")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Update category {}", categoryDto);
        return categoryService.updateCategory(categoryDto);
    }

}
