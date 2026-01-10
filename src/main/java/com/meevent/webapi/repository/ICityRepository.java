package com.meevent.webapi.repository;

import com.meevent.webapi.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICityRepository extends JpaRepository<City,Long> {
}
