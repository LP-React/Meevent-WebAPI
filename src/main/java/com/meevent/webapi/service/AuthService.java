package com.meevent.webapi.service;

import com.meevent.webapi.dto.request.LoginRequest;
import com.meevent.webapi.dto.request.RegisterRequest;
import com.meevent.webapi.dto.response.AuthResponse;
import com.meevent.webapi.model.Ciudad;
import com.meevent.webapi.model.Usuario;
import com.meevent.webapi.model.enums.UserRol;
import com.meevent.webapi.repository.ICiudadRepository;
import com.meevent.webapi.repository.IUsuarioRepository;
import com.meevent.webapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUsuarioRepository usuarioRepository;
    private final ICiudadRepository ciudadRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByCorreoElectronico(request.correoElectronico())
                .orElseThrow(() ->
                        new IllegalArgumentException("Credenciales inválidas")
                );

        if (!usuario.getCuentaActiva()) {
            throw new IllegalStateException("Cuenta deshabilitada");
        }

        if (!passwordEncoder.matches(
                request.contrasena(),
                usuario.getContrasenaHash()
        )) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(usuario.getCorreoElectronico());

        return new AuthResponse(
                jwtService.generateToken(userDetails)
        );
    }

    public AuthResponse register(RegisterRequest request) {

        if (usuarioRepository.existsByCorreoElectronico(request.correoElectronico())) {
            throw new IllegalArgumentException("El correo que ingreso ya se encuentra registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.nombreCompleto());
        usuario.setCorreoElectronico(request.correoElectronico());
        usuario.setContrasenaHash(
                passwordEncoder.encode(request.contrasena())
        );
        usuario.setTipoUsuario(UserRol.normal);
        usuario.setNumeroTelefono(request.numeroTelefono());
        usuario.setFechaNacimiento(request.fechaNacimiento());
        usuario.setCuentaActiva(true);
        usuario.setEmailVerificado(false);

        Ciudad ciudad = ciudadRepository.findById(request.idCiudad())
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada"));

        usuario.setCiudad(ciudad);

        usuarioRepository.save(usuario);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(usuario.getCorreoElectronico());

        return new AuthResponse(
                jwtService.generateToken(userDetails)
        );
    }
}
