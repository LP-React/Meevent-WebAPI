package com.meevent.webapi.repository;

import com.meevent.webapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoElectronico(String email);
    boolean existsByCorreoElectronico(String correoElectronico);
}
