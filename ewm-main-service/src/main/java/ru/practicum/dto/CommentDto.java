package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @NotNull
    private String text;
    private String date;
    private int rating;
    private UserShortDto author;
    private long eventId;
    private long id;
}
