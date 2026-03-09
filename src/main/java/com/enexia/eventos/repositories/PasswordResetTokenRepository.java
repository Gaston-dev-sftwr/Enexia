package com.enexia.eventos.repositories;


import com.enexia.eventos.models.PasswordResetToken;
import com.enexia.eventos.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Método vital: Buscamos el token que viene de la URL del correo
    Optional<PasswordResetToken> findByToken(String token);

    // Opcional: Para limpiar tokens viejos del mismo usuario
    void deleteByUsuario(Usuario usuario);
}