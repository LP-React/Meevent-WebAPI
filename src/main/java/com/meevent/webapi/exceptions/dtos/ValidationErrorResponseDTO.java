package com.meevent.webapi.exceptions.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponseDTO {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String path;
    private List<FieldErrorDTO> fieldErrors;
}

