package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(max = 120, message = "Максимальный размер заголовка 120 символов")
    @Size(min = 3, message = "Минимальный размер заголовка 3 символа")
    private String title;

    @NotBlank
    @Size(max = 2000, message = "Максимальный размер аннотации 2000 символов")
    @Size(min = 20, message = "Минимальный размер аннотации 20 символов")
    private String annotation;

    @NotBlank
    @Size(max = 7000, message = "Максимальный размер описания события 7000 символов")
    @Size(min = 20, message = "Минимальный размер описания события 20 символов")
    private String description;

    @NotNull
    private long category;

    @NotNull
    private String eventDate;
    private boolean paid;
    @NotNull
    private LocationDto location;
    private int participantLimit;
    private boolean requestModeration; //Нужна ли пре-модерация заявок на участие
}
