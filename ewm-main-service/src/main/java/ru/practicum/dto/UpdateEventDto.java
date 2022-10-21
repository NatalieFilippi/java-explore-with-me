package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.Category;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDto {
    private String title;
    private String annotation;
    private String description;
    private Category category;
    private String eventDate;
    @NotNull
    private long eventId;
    private boolean paid;
    private int participantLimit;
}
