package com.meevent.webapi.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id")
    private Long countryId;

    @Column(name = "country_name", nullable = false, length = 100)
    private String countryName;

    @Column(name = "iso_code", nullable = false, length = 3)
    private String isoCode;

}
