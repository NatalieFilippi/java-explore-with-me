package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminUpdateEventRequest {
    private String title;
    private String annotation;
    private String description;
    private long category;
    private String eventDate;
    private boolean paid;
    private Location location;
    private int participantLimit;
    private boolean requestModeration; //Нужна ли пре-модерация заявок на участие
}
