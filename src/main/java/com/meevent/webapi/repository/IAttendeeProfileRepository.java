package com.meevent.webapi.repository;

import com.meevent.webapi.model.AttendeeProfile;
import com.meevent.webapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAttendeeProfileRepository extends JpaRepository<AttendeeProfile, Long> {

    boolean existsByPhoneE164(String phoneE164);

    Optional<AttendeeProfile> findByUser(User user);

    boolean existsByPhoneE164AndAttendeeProfileIdNot(String phoneE164, Long id);
}
