package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {
    @NotBlank(message = "Комментарий не может быть пустым.")
    @Size(max = 2000, message = "Максимальный размер комментария 2000 символов!")
    @Size(min = 2, message = "Минимальный размер комментария 2 символа!")
    private String text;
}
