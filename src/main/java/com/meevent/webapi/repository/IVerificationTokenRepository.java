package com.meevent.webapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meevent.webapi.dto.request.VerificationToken;

@Repository
public interface IVerificationTokenRepository extends JpaRepository<VerificationToken, Long>{

    Optional<VerificationToken> findByToken(String token);

}
