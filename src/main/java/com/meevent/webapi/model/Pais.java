package com.meevent.webapi.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "paises")
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pais")
    private Long idPais;

    @Column(name = "nombre_pais", nullable = false, length = 100)
    private String nombrePais;

    @Column(name = "codigo_iso", nullable = false, length = 3)
    private String codigoIso;
}
