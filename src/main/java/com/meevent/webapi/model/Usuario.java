package com.meevent.webapi.model;

import com.meevent.webapi.model.enums.UserRol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String correoElectronico;

    @Column(nullable = false)
    private String contrasenaHash;

    private String numeroTelefono;
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRol tipoUsuario;

    private Boolean emailVerificado = false;
    private Boolean cuentaActiva = true;

    @ManyToOne
    @JoinColumn(name = "id_ciudad")
    private Ciudad ciudad;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
