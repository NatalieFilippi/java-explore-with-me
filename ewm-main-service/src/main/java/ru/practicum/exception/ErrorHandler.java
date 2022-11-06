package ru.practicum.exception;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(final MethodArgumentNotValidException e) {
        ApiError apiError = new ApiError();
        apiError.setErrors(Collections.emptyList());
        apiError.setReason("For the requested operation the conditions are not met.");
        apiError.setMessage("Only pending or canceled events can be changed");
        apiError.setStatus("FORBIDDEN");
        apiError.setTimestamp(String.valueOf(LocalDateTime.now()));
        return apiError;
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        ApiError apiError = new ApiError();
        apiError.setReason("Integrity constraint has been violated");
        apiError.setMessage(e.getMessage());
        apiError.setStatus("CONFLICT");
        apiError.setTimestamp(String.valueOf(LocalDateTime.now()));
        return apiError;
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final ObjectNotFoundException e) {
        ApiError apiError = new ApiError();
        apiError.setReason("The required object was not found.");
        apiError.setMessage(e.getMessage());
        apiError.setStatus("NOT_FOUND");
        apiError.setTimestamp(String.valueOf(LocalDateTime.now()));
        return apiError;
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConversionFailedException(final ConversionFailedException e) {
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", "Unknown state: UNSUPPORTED_STATUS");
    }

}
