package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.Category;
import ru.practicum.model.Location;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    private long id;
    @NotNull
    private String title;
    @NotNull
    private String annotation;
    @NotNull
    private String description;
    @NotNull
    private Category category;
    @NotNull
    private String eventDate;
    private boolean paid;
    private long views;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Location location;
    private String createdOn;
    private String publishedOn;
    private int confirmedRequests;
    private int participantLimit;
    private boolean requestModeration; //Нужна ли пре-модерация заявок на участие
    private String state;
}
