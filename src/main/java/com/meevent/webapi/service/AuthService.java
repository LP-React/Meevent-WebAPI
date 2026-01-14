package com.meevent.webapi.service;

import com.meevent.webapi.dto.request.LoginRequest;
import com.meevent.webapi.dto.request.RegisterRequest;
import com.meevent.webapi.dto.response.AuthResponse;
import com.meevent.webapi.model.AttendeeProfile;
import com.meevent.webapi.model.City;
import com.meevent.webapi.model.User;
import com.meevent.webapi.model.enums.UserRol;
import com.meevent.webapi.model.enums.UserVerificationStatus;
import com.meevent.webapi.repository.IAttendeeProfileRepository;
import com.meevent.webapi.repository.ICityRepository;
import com.meevent.webapi.repository.IUserRepository;
import com.meevent.webapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepository;
    private final ICityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final IAttendeeProfileRepository attendeeProfileRepository;

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmailIgnoreCase(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid credentials")
                );

        if (!user.getActive()) {
            throw new IllegalStateException("Account is disabled");
        }

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        return new AuthResponse(
                jwtService.generateToken(userDetails)
        );
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        String countryCode = request.countryCode();
        String phoneNumber = request.phoneNumber();

        String phoneE164 = null;

        if (countryCode != null && phoneNumber != null) {
            phoneE164 = countryCode + phoneNumber;
        }

        if (phoneE164 != null && userRepository.existsByPhoneE164(phoneE164)) {
            throw new IllegalArgumentException("Phone number is already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setCountryCode(request.countryCode());
        user.setPhoneNumber(request.phoneNumber());
        user.setPhoneE164(phoneE164);
        user.setActive(true);
        user.setVerificationStatus(UserVerificationStatus.NOT_VERIFIED);

        userRepository.save(user);

        City city = cityRepository.findById(request.cityId())
                .orElseThrow(() ->
                        new IllegalArgumentException("City not found")
                );

        AttendeeProfile attendeeProfile = new AttendeeProfile();
        attendeeProfile.setUser(user);
        attendeeProfile.setFullName(request.fullName());
        attendeeProfile.setBirthDate(request.birthDate());
        attendeeProfile.setCity(city);

        attendeeProfileRepository.save(attendeeProfile);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        return new AuthResponse(
                jwtService.generateToken(userDetails)
        );
    }
}
