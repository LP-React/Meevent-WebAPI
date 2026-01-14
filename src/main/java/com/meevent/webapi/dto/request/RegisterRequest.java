package com.meevent.webapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequest(
        /* ========== Attendee Profile ========== */
        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 150)
        @JsonProperty("full_name")
        String fullName,

        @Past(message = "Birth date must be in the past")
        @JsonProperty("birth_date")
        LocalDate birthDate,

        @NotNull(message = "City is required")
        @JsonProperty("city_id")
        Long cityId,

        /* ========== User Identity ========== */
        @NotBlank(message = "Email is required")
        @Email
        @Size(max = 150)
        @JsonProperty("email")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8)
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
                message = "Password must contain uppercase, lowercase, number and special character"
        )
        @JsonProperty("password")
        String password,

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
