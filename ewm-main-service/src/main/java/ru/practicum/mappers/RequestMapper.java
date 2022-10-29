package ru.practicum.mappers;

import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(request.getId());
        requestDto.setRequester(request.getRequester());
        requestDto.setStatus(request.getStatus());
        requestDto.setEvent(request.getEvent());
        requestDto.setCreated(request.getCreated().format(formatter));
        return requestDto;
    }

    public static ParticipationRequest toParticipationRequest(ParticipationRequestDto requestDto) {
        ParticipationRequest request = new ParticipationRequest();
        request.setId(requestDto.getId());
        request.setRequester(requestDto.getRequester());
        request.setStatus(requestDto.getStatus());
        request.setEvent(requestDto.getEvent());
        request.setCreated(LocalDateTime.parse(requestDto.getCreated()));
        return request;
    }
}
