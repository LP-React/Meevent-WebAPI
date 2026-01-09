package com.meevent.webapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequest(
      @NotBlank(message = "El nombre completo es obligatorio")
      @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
      @JsonProperty("nombre_completo")
      String nombreCompleto,

      @NotBlank(message = "El correo electrónico es obligatorio")
      @Email(message = "El formato del correo no es válido")
      @Size(max = 150, message = "El correo no puede exceder 150 caracteres")
      @JsonProperty("correo_electronico")
      String correoElectronico,

      @NotBlank(message = "La contraseña es obligatoria")
      @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
      @Pattern(
              regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
              message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial"
      )
      @JsonProperty("contrasenia")
      String contrasena,

      @Size(min = 9, max = 20, message = "El número de teléfono debe tener entre 9 y 20 caracteres")
      @Pattern(regexp = "^[0-9]+$", message = "El número de teléfono solo debe contener dígitos")
      @JsonProperty("numero_telefono")
      String numeroTelefono,

      @Past(message = "La fecha de nacimiento debe ser en el pasado")
      @JsonProperty("fecha_nacimiento")
      LocalDate fechaNacimiento,

      @JsonProperty("id_ciudad")
      @NotNull(message = "La ciudad es obligatoria")
      Long idCiudad
) {}
