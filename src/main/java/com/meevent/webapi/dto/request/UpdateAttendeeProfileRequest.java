package com.meevent.webapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateAttendeeProfileRequest(
        @Size(min = 3, max = 150)
        @JsonProperty("full_name")
        String fullName,

        @JsonProperty("city_id")
        Long cityId,

        @JsonProperty("birth_date")
        LocalDate birthDate,

        @Pattern(
                regexp = "^\\+[1-9][0-9]{0,3}$",
                message = "Invalid country code format"
        )
        @JsonProperty("country_code")
        String countryCode,

        @Pattern(
                regexp = "^[0-9]{9,20}$",
                message = "Phone number must contain only digits"
        )
        @JsonProperty("phone_number")
        String phoneNumber

) {}
