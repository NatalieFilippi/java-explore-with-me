package ru.practicum.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dao.CategoryRepository;
import ru.practicum.dao.CompilationRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dao.UserRepository;
import ru.practicum.dto.*;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.mappers.UserMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService implements AdminSrv {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Autowired
    public AdminService(UserRepository userRepository, CategoryRepository categoryRepository, EventRepository eventRepository, CompilationRepository compilationRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.compilationRepository = compilationRepository;
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
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        List<Event> events = compilationDto.getEvents().stream()
                .map(id -> eventRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Event with id=" + id + " was not found.")))
                .collect(Collectors.toList());

        Compilation compilation = CompilationMapper.toCompilation(compilationDto);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(long compId) {
        compilationRepository.findById(compId).orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " was not found."));
        compilationRepository.deleteById(compId);
    }

    @Override
    public void deleteEventFromCollection(long compId, long eventId) {
        compilationRepository
                .findEventFromCompilation(compId, eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event " + eventId + " in the compilation with id=" + compId + " was not found."));
        compilationRepository.deleteEventFromCompilation(compId, eventId);
    }

    @Override
    public void addEventToCompilation(long compId, long eventId) {
        compilationRepository.findById(compId).orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " was not found."));

        if (compilationRepository.findEventFromCompilation(compId, eventId).isPresent()) {
            throw new ValidationException("The event " + eventId + " is already present in the compilation " + compId);
        }

        compilationRepository.addEventToCompilation(compId, eventId);
    }

    @Override
    public void unpinCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " was not found."));
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    public void pinCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " was not found."));
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

}
