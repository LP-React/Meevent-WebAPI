package com.meevent.webapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 150, message = "Full name must be between 3 and 150 characters")
        @JsonProperty("full_name")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        @JsonProperty("email")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        @JsonProperty("password")
        String password,

        @Size(min = 9, max = 20, message = "Phone number must be between 9 and 20 digits")
        @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
        @JsonProperty("phone_number")
        String phoneNumber,

        @Past(message = "Birth date must be in the past")
        @JsonProperty("birth_date")
        LocalDate birthDate,

        @NotNull(message = "City is required")
        @JsonProperty("city_id")
        Long cityId,

        @Pattern(
                regexp = "^\\+[1-9][0-9]{0,3}$",
                message = "Invalid country code format"
        )
        @JsonProperty("country_code")
        String countryCode

) {}
