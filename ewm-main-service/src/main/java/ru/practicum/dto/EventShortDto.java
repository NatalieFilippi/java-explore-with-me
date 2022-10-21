package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.Category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private long id;
    @NotNull
    private String title;
    @NotBlank
    private String annotation;
    @NotNull
    private Category category;
    @NotNull
    private String eventDate;
    @NotNull
    private boolean paid;
    private int views;
    @NotNull
    private UserShortDto initiator;
    private int confirmedRequests; //Количество одобренных заявок на участие в данном событии
}
