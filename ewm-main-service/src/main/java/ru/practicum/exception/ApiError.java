package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private List<String> errors;
    private String message; //Сообщение об ошибке
    private String reason; //Общее описание причины ошибки
    private String status;
    private String timestamp;
}
