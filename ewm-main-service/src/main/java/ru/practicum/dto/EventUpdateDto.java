package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateDto {
    @JsonProperty(value = "eventId")
    private long id;
    @NotNull
    private String title;
    @NotNull
    private String annotation;
    private String description;
    @NotNull
    private long category;
    @NotNull
    private String eventDate;
    @NotNull
    private boolean paid;
    private int confirmedRequests;
    private int participantLimit;
}
