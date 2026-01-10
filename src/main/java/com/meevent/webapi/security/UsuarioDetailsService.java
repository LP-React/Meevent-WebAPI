package com.meevent.webapi.security;

import com.meevent.webapi.model.Usuario;
import com.meevent.webapi.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final IUsuarioRepository usuarioRepository;


    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario u = usuarioRepository.findByCorreoElectronico(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe"));

        return new UsuarioDetails(u);
    }
}
