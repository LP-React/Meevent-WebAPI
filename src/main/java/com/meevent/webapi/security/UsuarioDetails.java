package com.meevent.webapi.security;

import com.meevent.webapi.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class UsuarioDetails implements UserDetails {
    private final Long id;
    private final String email;
    private final String password;
    private final boolean activo;
    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioDetails(Usuario usuario) {
        this.id = usuario.getIdUsuario();
        this.email = usuario.getCorreoElectronico();
        this.password = usuario.getContrasenaHash();
        this.activo = usuario.getCuentaActiva();
        this.authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getTipoUsuario().name())
        );
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
