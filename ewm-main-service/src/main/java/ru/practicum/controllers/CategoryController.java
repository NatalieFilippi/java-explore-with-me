package ru.practicum.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.services.AdminSrv;
import ru.practicum.services.CategorySrv;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/categories")
public class CategoryController {
    private final AdminSrv service;
    private final CategorySrv categoryService;

    public CategoryController(AdminSrv service, CategorySrv categoryService) {
        this.service = service;
        this.categoryService = categoryService;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable long catId) {
        log.info("Get category with id={}", catId);
        return categoryService.getCategory(catId);
    }

    @GetMapping
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                           @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Get categories with from={}, size={}", from, size);
        return categoryService.getCategories(from, size);
    }

}
