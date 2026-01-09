package com.meevent.webapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ciudades")
public class Ciudad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ciudad")
    private Long idCiudad;

    @Column(name = "nombre_ciudad", nullable = false, length = 100)
    private String nombreCiudad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pais_id", nullable = false)
    private Pais pais;
}
