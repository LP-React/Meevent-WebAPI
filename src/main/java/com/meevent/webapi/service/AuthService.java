package com.meevent.webapi.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.meevent.webapi.dto.request.ForgotPasswordRequest;
import com.meevent.webapi.dto.request.GoogleLoginRequest;
import com.meevent.webapi.dto.request.LoginRequest;
import com.meevent.webapi.dto.request.RegisterRequest;
import com.meevent.webapi.dto.request.ResetPasswordRequest;
import com.meevent.webapi.dto.response.AuthResponse;
import com.meevent.webapi.exceptions.dtos.InvalidTokenException;
import com.meevent.webapi.model.AttendeeProfile;
import com.meevent.webapi.model.City;
import com.meevent.webapi.model.PasswordResetToken;
import com.meevent.webapi.model.User;
import com.meevent.webapi.model.VerificationToken;
import com.meevent.webapi.model.enums.AuthProvider;
import com.meevent.webapi.model.enums.UserVerificationStatus;
import com.meevent.webapi.repository.IAttendeeProfileRepository;
import com.meevent.webapi.repository.ICityRepository;
import com.meevent.webapi.repository.IPasswordResetTokenRepository;
import com.meevent.webapi.repository.IUserRepository;
import com.meevent.webapi.repository.IVerificationTokenRepository;
import com.meevent.webapi.security.GoogleTokenVerifier;
import com.meevent.webapi.security.JwtService;
import com.meevent.webapi.security.UserDetailsImpl;
import com.meevent.webapi.service.IMailService.IMailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final IMailService mailService; /* <-- Azure service implementation */
    private final IVerificationTokenRepository tokenRepository;
    private final IPasswordResetTokenRepository passwordResetTokenRepository;
    private final IUserRepository userRepository;
    private final ICityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final IAttendeeProfileRepository attendeeProfileRepository;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmailIgnoreCase(request.email())
                .orElseThrow(()
                        -> new IllegalArgumentException("Invalid credentials")
                );

        if (!user.getActive()) {
            LOGGER.warn("Login attempt on disabled account: email={}", request.email());
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (user.getVerificationStatus() != UserVerificationStatus.VERIFIED) {
            LOGGER.warn("Login attempt on unverified account: email={}", request.email());
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (user.getPasswordHash() == null) {
            LOGGER.warn("Login attempt with password on OAuth-only account: email={}", request.email());
            throw new IllegalArgumentException("Invalid credentials");
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
    public void register(RegisterRequest request) {

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
        LOGGER.info("User registered successfully: email={}", user.getEmail());

        String tokenValue = UUID.randomUUID().toString();
        VerificationToken token = new VerificationToken(tokenValue, user, 1); /*<--- An hour to expire the token */
        tokenRepository.save(token);
        String subject = "Verifica tu cuenta en Meevent";
        String message = "¡Hola! Gracias por registrarte. Haz clic en el siguiente enlace para verificar tu cuenta: ";

        mailService.sendVerificationEmail(user.getEmail(), subject, message, tokenValue);

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
    }

    @Transactional
    public AuthResponse verifyEmail(String tokenValue) {
        VerificationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> {
                    LOGGER.warn("Verification attempt with unknown token");
                    return new InvalidTokenException("Token invalido o expirado");
                });

        if (token.isUsed()) {
            LOGGER.warn("Verification attempt with used token: tokenId={}", token.getId());
            throw new InvalidTokenException("Token invalido o expirado");
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            LOGGER.warn("Verification attempt with expired token: tokenId={}", token.getId());
            throw new InvalidTokenException("Token invalido o expirado");
        }

        // User updated
        User user = token.getToUser();
        user.setVerificationStatus(UserVerificationStatus.VERIFIED);
        userRepository.save(user);

        // Change state of token from PENDING to USED creo
        token.setUsed(true);
        tokenRepository.save(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        return new AuthResponse(jwtService.generateToken(userDetails));
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.email()).ifPresent(user -> {
            passwordResetTokenRepository.deleteByToUser(user);

            String rawToken = UUID.randomUUID().toString();
            String tokenHash = sha256(rawToken);

            PasswordResetToken resetToken = new PasswordResetToken(tokenHash, user, 30);
            passwordResetTokenRepository.save(resetToken);

            mailService.sendPasswordResetEmail(user.getEmail(), rawToken);
            LOGGER.info("Password reset email sent: email={}", user.getEmail());
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String tokenHash = sha256(request.token());

        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> {
                    LOGGER.warn("Password reset attempt with unknown token");
                    return new InvalidTokenException("Token invalido o expirado");
                });

        if (resetToken.isUsed()) {
            LOGGER.warn("Password reset attempt with used token: tokenId={}", resetToken.getId());
            throw new InvalidTokenException("Token invalido o expirado");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            LOGGER.warn("Password reset attempt with expired token: tokenId={}", resetToken.getId());
            throw new InvalidTokenException("Token invalido o expirado");
        }

        User user = resetToken.getToUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        mailService.sendPasswordChangeConfirmationEmail(user.getEmail());
        LOGGER.info("Password reset successful: email={}", user.getEmail());
    }

    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(request.idToken());
        if (payload == null) {
            LOGGER.warn("Google login attempt with invalid/expired ID token");
            throw new IllegalArgumentException("Invalid Google ID token");
        }

        String googleSub = payload.getSubject();
        String email = payload.getEmail();
        Boolean emailVerified = payload.getEmailVerified();
        String fullName = (String) payload.get("name");

        LOGGER.info("Google login: sub={}, email={}, emailVerified={}", googleSub, email, emailVerified);

        if (emailVerified == null || !emailVerified) {
            LOGGER.warn("Google login rejected: email not verified by Google, email={}", email);
            throw new IllegalArgumentException("Google account email is not verified");
        }

        Optional<User> existingByGoogleSub = userRepository.findByGoogleSub(googleSub);
        if (existingByGoogleSub.isPresent()) {
            return handleExistingGoogleUser(existingByGoogleSub.get());
        }

        Optional<User> existingByEmail = userRepository.findByEmailIgnoreCase(email);
        if (existingByEmail.isPresent()) {
            return handleAccountLinking(existingByEmail.get(), googleSub);
        }

        return handleNewGoogleUser(email, googleSub, fullName);
    }

    private AuthResponse handleExistingGoogleUser(User user) {
        if (!user.getActive()) {
            LOGGER.warn("Google login attempt on disabled account: userId={}", user.getUserId());
            throw new IllegalArgumentException("Invalid credentials");
        }
        LOGGER.info("Google login: returning user, userId={}", user.getUserId());
        return generateAuthResponse(user);
    }

    private AuthResponse handleAccountLinking(User user, String googleSub) {
        if (!user.getActive()) {
            LOGGER.warn("Google account linking attempt on disabled account: userId={}", user.getUserId());
            throw new IllegalArgumentException("Invalid credentials");
        }

        LOGGER.info("Google login: linking Google account to existing LOCAL user, userId={}, googleSub={}", user.getUserId(), googleSub);
        user.setGoogleSub(googleSub);

        if (user.getVerificationStatus() != UserVerificationStatus.VERIFIED) {
            LOGGER.info("Google login: auto-verifying previously unverified LOCAL user, userId={}", user.getUserId());
            user.setVerificationStatus(UserVerificationStatus.VERIFIED);
        }

        userRepository.save(user);
        return generateAuthResponse(user);
    }

    private AuthResponse handleNewGoogleUser(String email, String googleSub, String fullName) {
        LOGGER.info("Google login: creating new user, email={}, googleSub={}", email, googleSub);

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(null);
        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setGoogleSub(googleSub);
        user.setVerificationStatus(UserVerificationStatus.VERIFIED);
        user.setActive(true);
        userRepository.save(user);

        AttendeeProfile attendeeProfile = new AttendeeProfile();
        attendeeProfile.setUser(user);
        attendeeProfile.setFullName(fullName != null ? fullName : email);
        attendeeProfileRepository.save(attendeeProfile);

        LOGGER.info("Google login: new user and profile created, userId={}, profileId={}", user.getUserId(), attendeeProfile.getAttendeeProfileId());
        return generateAuthResponse(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        UserDetails userDetails = new UserDetailsImpl(user);
        return new AuthResponse(jwtService.generateToken(userDetails));
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
