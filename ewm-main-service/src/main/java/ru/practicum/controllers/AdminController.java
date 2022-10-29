package ru.practicum.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.model.Event;
import ru.practicum.services.AdminSrv;
import ru.practicum.services.CategorySrv;
import ru.practicum.services.EventSrv;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping(path = "/admin")
public class AdminController {
    private final AdminSrv service;
    private final CategorySrv categoryService;
    private final EventSrv eventService;

    public AdminController(AdminSrv service, CategorySrv categoryService, EventSrv eventService) {
        this.service = service;
        this.categoryService = categoryService;
        this.eventService = eventService;
    }

    /*
    API для работы с пользователями
     */

    @PostMapping("/users")
    public UserDto createUser(@Valid @RequestBody NewUserDto userDto) {
        log.info("Create user {}", userDto);
        return service.createUser(userDto);
    }

    @GetMapping("/users")
    public List<UserDto> findAll(@RequestParam(required = false, defaultValue = "") Set<Long> ids,
                                 @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                 @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Get events with ids {}, from {}, size {}", ids, from, size);
        return service.findAll(ids, from, size);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Delete user with id={}", userId);
        service.deleteUser(userId);
    }


    /*
    API для работы с категориями
     */

    @PostMapping("/categories")
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto categoryDto) {
        log.info("Create category {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@PathVariable long catId) {
        log.info("Delete category with id={}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/categories")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Update category {}", categoryDto);
        return categoryService.updateCategory(categoryDto);
    }


    /*
    API для работы с подборками
     */

    @PostMapping("/compilations") //добавить подборку
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Create compilation {}", compilationDto);
        return service.createCompilation(compilationDto);
    }

    @DeleteMapping("/compilations/{compId}") //удалить подборку
    public void deleteCompilation(@PathVariable long compId) {
        log.info("Delete compilation with id={}", compId);
        service.deleteCompilation(compId);
    }

    @DeleteMapping("/compilations/{compId}/events/{eventId}") //удалить событие из подборки
    public void deleteEventFromCollection(@PathVariable long compId,
                                          @PathVariable long eventId) {
        log.info("Delete event {} from compilation with id={}", eventId, compId);
        service.deleteEventFromCollection(compId, eventId);
    }

    @PatchMapping("/compilations/{compId}/events/{eventId}") //добавить событие в подборку
    public void addEventToCompilation(@PathVariable long compId,
                                      @PathVariable long eventId) {
        log.info("Add event {} in compilation with id={}", eventId, compId);
        service.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/compilations/{compId}/pin") //открепить подборку
    public void unpinCompilation(@PathVariable long compId) {
        log.info("Unpin compilation with id={}", compId);
        service.unpinCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}/pin") //закрепить подборку
    public void pinCompilation(@PathVariable long compId) {
        log.info("Pin compilation with id={}", compId);
        service.pinCompilation(compId);
    }


    /*
    API для работы с событиями
     */

    @GetMapping("/events")
    public List<EventFullDto> findAllEvents(@RequestParam(required = false, defaultValue = "") List<Long> users,
                                            @RequestParam(required = false, defaultValue = "") List<Event.State> states,
                                            @RequestParam(required = false, defaultValue = "") List<Long> categories,
                                            @RequestParam(required = false, defaultValue = "") String rangeStart,
                                            @RequestParam(required = false, defaultValue = "") String rangeEnd,
                                            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                            @Positive @RequestParam(required = false, defaultValue = "10") int size
    ) {
        log.info("Get events with users {}, states {}, categories {}, rangeStart {}, rangeEnd {}, from {}, size {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.findAllEventsForAdmin(users, categories, states, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable long eventId) {
        log.info("Publish event with id={}", eventId);
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable long eventId) {
        log.info("Reject event with id={}", eventId);
        return eventService.rejectEvent(eventId);
    }

    @PutMapping("/events/{eventId}")
    public EventFullDto adminUpdateEvent(@PathVariable long eventId,
                                         @RequestBody AdminUpdateEventRequest eventDto) {
        log.info("Admin update event with id={}", eventId);
        return eventService.adminUpdateEvent(eventId, eventDto);
    }

}
