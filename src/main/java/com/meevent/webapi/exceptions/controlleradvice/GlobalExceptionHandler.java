package com.meevent.webapi.exceptions.controlleradvice;

import com.meevent.webapi.exceptions.dtos.ExceptionDTO;
import com.meevent.webapi.exceptions.dtos.FieldErrorDTO;
import com.meevent.webapi.exceptions.dtos.NotFoundException;
import com.meevent.webapi.exceptions.dtos.ValidationErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ExceptionDTO> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        ExceptionDTO dto = new ExceptionDTO();
        dto.setStatus(status.value());
        dto.setError(status.getReasonPhrase());
        dto.setMessage(message);
        dto.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(dto);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionDTO> handleAuthException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        LOGGER.warn("Authentication error: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDTO> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        LOGGER.warn("Access denied: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "No tienes permisos para esta acción",
                request
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDTO> handleBusiness(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        LOGGER.error("Business exception", ex);
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        List<FieldErrorDTO> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorDTO(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        ValidationErrorResponseDTO response =
                ValidationErrorResponseDTO.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Validation Error")
                        .path(request.getRequestURI())
                        .fieldErrors(fieldErrors)
                        .build();

        return ResponseEntity.badRequest().body(response);
    }

}
