package com.meevent.webapi.repository;

import com.meevent.webapi.model.AttendeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAttendeeProfileRepository extends JpaRepository<AttendeeProfile, Long> {

    boolean existsByPhoneE164(String phoneE164);

}
