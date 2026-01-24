package com.meevent.webapi.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meevent.webapi.dto.request.LoginRequest;
import com.meevent.webapi.dto.request.RegisterRequest;
import com.meevent.webapi.dto.request.VerificationToken;
import com.meevent.webapi.dto.response.AuthResponse;
import com.meevent.webapi.model.AttendeeProfile;
import com.meevent.webapi.model.City;
import com.meevent.webapi.model.User;
import com.meevent.webapi.model.enums.UserVerificationStatus;
import com.meevent.webapi.repository.IAttendeeProfileRepository;
import com.meevent.webapi.repository.ICityRepository;
import com.meevent.webapi.repository.IUserRepository;
import com.meevent.webapi.repository.IVerificationTokenRepository;
import com.meevent.webapi.security.JwtService;
import com.meevent.webapi.service.IMailService.IMailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IMailService mailService; /* <-- Azure service implementation */
    private final IVerificationTokenRepository tokenRepository;
    //private final MailServiceImpl mailService; (for gmail testing)
    private final IUserRepository userRepository;
    private final ICityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final IAttendeeProfileRepository attendeeProfileRepository;

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmailIgnoreCase(request.email())
                .orElseThrow(()
                        -> new IllegalArgumentException("Invalid credentials")
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

        UserDetails userDetails
                = userDetailsService.loadUserByUsername(user.getEmail());

        return new AuthResponse(
                jwtService.generateToken(userDetails)
        );
    }

    @Transactional
    @Async
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

        if (phoneE164 != null && attendeeProfileRepository.existsByPhoneE164(phoneE164)) {
            throw new IllegalArgumentException("Phone number is already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setActive(true);
        user.setVerificationStatus(UserVerificationStatus.PENDING); /* <--- change form not_verified to ---> pending */

        userRepository.save(user);

        String tokenValue = UUID.randomUUID().toString();
        VerificationToken token = new VerificationToken(tokenValue, user, 1); /*<--- An hour to expire the token */
        tokenRepository.save(token);
        String subject = "Verifica tu cuenta en Meevent";
        String message = "¡Hola! Gracias por registrarte. Haz clic en el siguiente enlace para verificar tu cuenta: ";

        String verificationLink = tokenValue;

        mailService.sendVerificationEmail(user.getEmail(), subject, message, verificationLink);

        City city = cityRepository.findById(request.cityId())
                .orElseThrow(()
                        -> new IllegalArgumentException("City not found")
                );

        AttendeeProfile attendeeProfile = new AttendeeProfile();
        attendeeProfile.setUser(user);
        attendeeProfile.setFullName(request.fullName());
        attendeeProfile.setBirthDate(request.birthDate());
        attendeeProfile.setCity(city);
        attendeeProfile.setCountryCode(request.countryCode());
        attendeeProfile.setPhoneNumber(request.phoneNumber());
        attendeeProfile.setPhoneE164(phoneE164);

        attendeeProfileRepository.save(attendeeProfile);

        UserDetails userDetails
                = userDetailsService.loadUserByUsername(user.getEmail());

        return new AuthResponse(
                jwtService.generateToken(userDetails)
        );
    }

    @Transactional
    public void verifyEmail(String tokenValue) {
        VerificationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Token no encontrado"));

        if (token.isUsed()) {
            throw new RuntimeException("Este token ya ha sido utilizado");
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        // User updated
        User user = token.getToUser();
        user.setVerificationStatus(UserVerificationStatus.VERIFIED);
        userRepository.save(user);

        // Change state of token from PENDING to USED creo 
        token.setUsed(true);
        tokenRepository.save(token);
    }
}
