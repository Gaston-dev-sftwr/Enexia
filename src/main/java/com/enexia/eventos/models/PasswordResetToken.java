package com.enexia.eventos.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    // Relación con tu clase Usuario base
    @OneToOne(targetEntity = Usuario.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    public PasswordResetToken() {}

    public PasswordResetToken(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
        // El código durará 15 minutos. Tiempo de sobra para cambiar la pass.
        this.fechaExpiracion = LocalDateTime.now().plusMinutes(15);
    }

    // Getters necesarios
    public String getToken() { return token; }
    public Usuario getUsuario() { return usuario; }

    // Método para saber si el tiempo ya pasó
    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(this.fechaExpiracion);
    }
}