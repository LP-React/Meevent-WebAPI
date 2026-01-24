package com.meevent.webapi.dto.response;

import java.time.LocalDateTime;

public record MessageResponse (
        String message,
        LocalDateTime timestamp
){
    public MessageResponse(String message) {
        this(message, LocalDateTime.now());
    }
}
