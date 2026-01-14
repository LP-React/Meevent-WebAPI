package com.meevent.webapi.exceptions.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldErrorDTO {
    private String field;
    private String message;
}

