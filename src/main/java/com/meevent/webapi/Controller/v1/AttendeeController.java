package com.meevent.webapi.Controller.v1;

import com.meevent.webapi.dto.request.UpdateAttendeeProfileRequest;
import com.meevent.webapi.dto.response.AttendeeProfileResponse;
import com.meevent.webapi.service.AttendeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attendees")
@RequiredArgsConstructor
public class AttendeeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendeeController.class);
    private final AttendeeService attendeeService;

    @PatchMapping("/profile")
    public ResponseEntity<AttendeeProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateAttendeeProfileRequest request
    ) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.debug("Request UPDATE recibido de: {}", userEmail);

        AttendeeProfileResponse response = attendeeService.updateProfile(userEmail, request);

        return ResponseEntity.ok(response);
    }
}
