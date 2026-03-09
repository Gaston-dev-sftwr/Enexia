package com.enexia.eventos.controllers;

import com.enexia.eventos.models.PasswordResetToken;
import com.enexia.eventos.models.Usuario;
import com.enexia.eventos.repositories.PasswordResetTokenRepository;
import com.enexia.eventos.repositories.UsuarioRepository;
import com.enexia.eventos.utils.ValidadorPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JavaMailSender mailSender; // Inyectamos el motor de correos directamente

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
            SimpleMailMessage mensaje = new SimpleMailMessage();

            // Los datos que configuramos antes
            mensaje.setFrom("soporte@enexia.com");
            mensaje.setTo("prueba@enexia.com"); // No importa que no exista, Mailtrap lo atrapa
            mensaje.setSubject("Prueba de Conexión Enexia");
            mensaje.setText("¡Hola! Si ves este mensaje en tu panel de Mailtrap, la configuración es un éxito.");

            mailSender.send(mensaje);

            return ResponseEntity.ok("Correo enviado a Mailtrap con éxito. Revisá tu bandeja virtual.");
        } catch (Exception e) {
            // Si algo falla (como las credenciales), lo veremos acá
            return ResponseEntity.status(500).body("Error al enviar el correo: " + e.getMessage());
        }
    }


    @PostMapping("/recuperar-password")
    public ResponseEntity<?> solicitarRecuperacion(@RequestParam String email) {


        try {
            // 1. Buscamos al usuario por su email
            Usuario usuario = usuarioRepository.findByEmail(email);
            if (usuario == null) {
                return new ResponseEntity<>("No existe un usuario con ese correo electrónico.", HttpStatus.NOT_FOUND);
            }

            // 2. Generamos un token aleatorio único
            String tokenUnico = UUID.randomUUID().toString();

            // 3. Guardamos el token en la base de datos vinculado al usuario
            // Nota: Si ya existía uno viejo, podrías borrarlo antes con tokenRepository.deleteByUsuario(usuario)
            PasswordResetToken resetToken = new PasswordResetToken(tokenUnico, usuario);
            passwordResetTokenRepository.save(resetToken);

            // 4. Preparamos el link que el usuario recibirá (apunta a tu front de cambio de pass)
            // El puerto 8081 es el que usás según tus propiedades
            String linkRecuperacion = baseUrl + "/web/mod_cambiar_password.html?token=" + tokenUnico;

            // 5. Enviamos el mail real usando lo que ya probamos
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("soporte@enexia.com");
            mensaje.setTo(email);
            mensaje.setSubject("Recuperación de Contraseña - ENEXIA");
            mensaje.setText("Hola " + usuario.getNombre() + ",\n\n" +
                    "Has solicitado restablecer tu contraseña. Haz clic en el siguiente enlace para continuar:\n" +
                    linkRecuperacion + "\n\n" +
                    "Este enlace expirará en 15 minutos.");

            mailSender.send(mensaje);

            return ResponseEntity.ok("Se ha enviado un correo con las instrucciones.");

        } catch (Exception e) {
            return new ResponseEntity<>("Error al procesar la solicitud: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestParam String token, @RequestParam String nuevaPassword) {
        try {
            // Ejecutamos la validación centralizada
            // Si falla, tira la excepción y el flujo salta directamente al catch
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
            // Acá 'e.getMessage()' será exactamente el error que definimos en el validador
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}