package com.enexia.eventos.controllers;

import com.enexia.eventos.models.PasswordResetToken;
import com.enexia.eventos.models.Usuario;
import com.enexia.eventos.repositories.PasswordResetTokenRepository;
import com.enexia.eventos.repositories.UsuarioRepository;
import com.enexia.eventos.services.ResendService; // Importamos tu nuevo servicio
import com.enexia.eventos.utils.ValidadorPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ResendService resendService; // Cambiamos JavaMailSender por ResendService

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ValidadorPassword validadorPassword;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/test-email")
    public ResponseEntity<?> probarEmail() {
        try {
            // Probamos con un HTML simple
            String contenidoHtml = "<h1>¡Conexión Exitosa!</h1><p>Si recibís esto, Enexia ya está hablando con la API de Resend.</p>";

            resendService.enviarCorreo("tu-email-de-prueba@gmail.com", "Prueba de API Resend - Enexia", contenidoHtml);

            return ResponseEntity.ok("Petición enviada a Resend con éxito. Revisá tu casilla.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en la API de Resend: " + e.getMessage());
        }
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<?> solicitarRecuperacion(@RequestParam String email) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(email);
            if (usuario == null) {
                return new ResponseEntity<>("No existe un usuario con ese correo electrónico.", HttpStatus.NOT_FOUND);
            }

            String tokenUnico = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(tokenUnico, usuario);
            passwordResetTokenRepository.save(resetToken);

            String linkRecuperacion = baseUrl + "/web/mod_cambiar_password.html?token=" + tokenUnico;

            // Preparamos un cuerpo HTML más profesional que el SimpleMailMessage
            String cuerpoHtml = String.format(
                    "<div style='font-family: Arial, sans-serif; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>" +
                            "<h2>Hola, %s</h2>" +
                            "<p>Has solicitado restablecer tu contraseña en <strong>ENEXIA</strong>.</p>" +
                            "<p>Haz clic en el siguiente botón para continuar:</p>" +
                            "<a href='%s' style='background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;'>Restablecer Contraseña</a>" +
                            "<p style='margin-top: 20px; font-size: 0.8em; color: #666;'>Este enlace expirará en 15 minutos.</p>" +
                            "</div>",
                    usuario.getNombre(), linkRecuperacion
            );

            // Enviamos el mail usando la API
            resendService.enviarCorreo(email, "Recuperación de Contraseña - ENEXIA", cuerpoHtml);

            return ResponseEntity.ok("Se ha enviado un correo con las instrucciones.");

        } catch (Exception e) {
            return new ResponseEntity<>("Error al procesar la solicitud: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestParam String token, @RequestParam String nuevaPassword) {
        try {
            validadorPassword.validar(nuevaPassword);

            Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByToken(token);
            if (tokenOptional.isEmpty() || tokenOptional.get().estaExpirado()) {
                return new ResponseEntity<>("Enlace inválido o expirado.", HttpStatus.BAD_REQUEST);
            }

            Usuario usuario = tokenOptional.get().getUsuario();
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            usuarioRepository.save(usuario);
            passwordResetTokenRepository.delete(tokenOptional.get());

            return ResponseEntity.ok("Contraseña actualizada con éxito.");

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}