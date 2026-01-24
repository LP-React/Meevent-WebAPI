package com.meevent.webapi.service;

import com.meevent.webapi.dto.request.UpdateAttendeeProfileRequest;
import com.meevent.webapi.dto.response.AttendeeProfileResponse;
import com.meevent.webapi.model.AttendeeProfile;
import com.meevent.webapi.model.City;
import com.meevent.webapi.model.User;
import com.meevent.webapi.repository.IAttendeeProfileRepository;
import com.meevent.webapi.repository.ICityRepository;
import com.meevent.webapi.repository.IUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttendeeService.class);

    private final IUserRepository userRepository;
    private final IAttendeeProfileRepository attendeeProfileRepository;
    private final ICityRepository cityRepository;

    @Transactional
    public void updateProfile(String userEmail, UpdateAttendeeProfileRequest request) {
        LOGGER.info("Actualizando perfil para: {}", userEmail);

        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        AttendeeProfile profile = attendeeProfileRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado"));

        // Campos básicos
        if (request.fullName() != null) profile.setFullName(request.fullName());
        if (request.birthDate() != null) profile.setBirthDate(request.birthDate());

        if (request.cityId() != null) {
            City city = cityRepository.findById(request.cityId())
                    .orElseThrow(() -> new EntityNotFoundException("Ciudad no encontrada"));
            profile.setCity(city);
        }

        // Teléfono y Country Code
        if (request.phoneNumber() != null) {
            LOGGER.info("Detectado nuevo phoneNumber en el request: '{}'", request.phoneNumber());
            profile.setPhoneNumber(request.phoneNumber());
        } else {
            LOGGER.warn("El phoneNumber llegó NULO en el request");
        }

        if (request.countryCode() != null) {
            LOGGER.info("Detectado nuevo countryCode en el request: '{}'", request.countryCode());
            profile.setCountryCode(request.countryCode());
        }

        if (request.phoneNumber() != null || request.countryCode() != null) {
            recalculateE164(profile);

            boolean exists = attendeeProfileRepository.existsByPhoneE164AndAttendeeProfileIdNot(
                    profile.getPhoneE164(),
                    profile.getAttendeeProfileId()
            );

            if (exists) {
                throw new RuntimeException("El número de teléfono ya está registrado por otro usuario");
            }
        }

        AttendeeProfile savedProfile = attendeeProfileRepository.save(profile);
        LOGGER.info("Perfil actualizado. E164 final: {}", savedProfile.getPhoneE164());

    }

    private void recalculateE164(AttendeeProfile profile) {
        String cc = profile.getCountryCode();
        String phone = profile.getPhoneNumber();

        if (cc == null || phone == null) {
            LOGGER.warn("No se pudo calcular E164: countryCode o phoneNumber son nulos");
            return;
        }

        // dejamos solo números en el teléfono
        String cleanPhone = phone.replaceAll("[^0-9]", "");

        // el country code debe tener el + y solo número
        String cleanCC = cc.trim();
        if (!cleanCC.startsWith("+")) {
            cleanCC = "+" + cleanCC.replaceAll("[^0-9]", "");
        } else {
            cleanCC = "+" + cleanCC.substring(1).replaceAll("[^0-9]", "");
        }

        String finalE164 = cleanCC + cleanPhone;
        profile.setPhoneE164(finalE164);
        LOGGER.debug("E164 generado: {}", finalE164);
    }

    private AttendeeProfileResponse mapToResponseRecord(AttendeeProfile entity) {
        return new AttendeeProfileResponse(
                entity.getAttendeeProfileId(),
                entity.getUser().getEmail(),
                entity.getFullName(),
                entity.getCity().getCityName(),
                entity.getCity().getCountry().getCountryName(),
                entity.getPhoneE164(),
                entity.getBirthDate()
        );
    }
}
