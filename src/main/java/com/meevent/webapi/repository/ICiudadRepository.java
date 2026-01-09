package com.meevent.webapi.repository;

import com.meevent.webapi.model.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICiudadRepository extends JpaRepository<Ciudad,Long> {
}
