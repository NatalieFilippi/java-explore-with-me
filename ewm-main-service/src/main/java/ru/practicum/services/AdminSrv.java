package ru.practicum.services;

import ru.practicum.dto.*;

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
    Работа с подборками
     */

    CompilationDto createCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(long compId);

    void deleteEventFromCollection(long compId, long eventId);

    void addEventToCompilation(long compId, long eventId);

    void unpinCompilation(long compId);

    void pinCompilation(long compId);
}
