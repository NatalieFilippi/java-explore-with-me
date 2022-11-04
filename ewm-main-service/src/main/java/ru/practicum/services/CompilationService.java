package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dao.CompilationRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService implements CompilationSrv {
    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto getCompilation(long compId) {
        return CompilationMapper.toCompilationDto(repository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Compilation with id=21 was not found.")));
    }

    @Override
    public List<CompilationDto> getCompilations(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return repository.findAll(pageable)
                .stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        List<Event> events = compilationDto.getEvents().stream()
                .map(id -> eventRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Event with id=" + id + " was not found.")))
                .collect(Collectors.toList());

        Compilation compilation = CompilationMapper.toCompilation(compilationDto);
        compilation.setEvents(events);
        repository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(long compId) {
        repository.findById(compId).orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " was not found."));
        repository.deleteById(compId);
    }

    @Override
    public void deleteEventFromCollection(long compId, long eventId) {
        repository
                .findEventFromCompilation(compId, eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event " + eventId + " in the compilation with id=" + compId + " was not found."));
        repository.deleteEventFromCompilation(compId, eventId);
    }

    @Override
    public void addEventToCompilation(long compId, long eventId) {
        repository.findById(compId).orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " was not found."));

        if (repository.findEventFromCompilation(compId, eventId).isPresent()) {
            throw new ValidationException("The event " + eventId + " is already present in the compilation " + compId);
        }

        repository.addEventToCompilation(compId, eventId);
    }

    @Override
    public void pinSwitcher(long compId, boolean tag) {
        Compilation compilation = repository.findById(compId).orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " was not found."));
        compilation.setPinned(tag);
        repository.save(compilation);
    }
}
