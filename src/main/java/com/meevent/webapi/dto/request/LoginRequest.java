package com.meevent.webapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
      @NotBlank(message = "El correo electrónico es obligatorio")
      @Email(message = "El formato del correo no es válido")
      @JsonProperty("correo_electronico")
      String correoElectronico,

      @NotBlank(message = "La contraseña es obligatoria")
      @JsonProperty("contrasenia")
      String contrasena

) {}
