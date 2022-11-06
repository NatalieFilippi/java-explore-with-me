package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.ParticipationRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    private long id;
    private long event;
    private String created;
    private long requester;
    private ParticipationRequest.StateRequest status;
}
