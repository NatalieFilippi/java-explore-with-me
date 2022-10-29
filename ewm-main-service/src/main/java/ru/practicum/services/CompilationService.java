package ru.practicum.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dao.CompilationRepository;
import ru.practicum.dto.CompilationDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mappers.CompilationMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationService implements CompilationSrv {
    private final CompilationRepository repository;

    public CompilationService(CompilationRepository repository) {
        this.repository = repository;
    }

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
}
