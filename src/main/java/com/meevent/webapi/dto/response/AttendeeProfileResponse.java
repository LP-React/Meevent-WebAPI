package com.meevent.webapi.dto.response;

import java.time.LocalDate;

public record AttendeeProfileResponse(
        Long profileId,
        String email,
        String fullName,
        String cityName,
        String countryName,
        String phoneNumber,
        LocalDate birthDate
) {}
