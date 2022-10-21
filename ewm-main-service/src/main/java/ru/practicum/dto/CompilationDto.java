package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    @NotNull
    private long id;
    private List<Long> events;
    @NotNull
    private boolean pinned;
    @NotNull
    private String title;
}
