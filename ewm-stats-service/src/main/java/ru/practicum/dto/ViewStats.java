package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStats {
    private String app; //Название сервиса
    private String uri; //URI сервиса
    private long hits;   //Количество просмотров
}
