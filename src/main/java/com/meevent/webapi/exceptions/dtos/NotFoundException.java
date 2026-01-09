package com.meevent.webapi.exceptions.dtos;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
