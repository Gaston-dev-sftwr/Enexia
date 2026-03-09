package com.enexia.eventos.controllers;

import com.enexia.eventos.dtos.RegistroDTO;
import com.enexia.eventos.dtos.UsuarioDTO;
import com.enexia.eventos.models.Organizador;
import com.enexia.eventos.models.Participante;
import com.enexia.eventos.models.Usuario;
import com.enexia.eventos.repositories.OrganizadorRepository;
import com.enexia.eventos.repositories.ParticipanteRepository;
import com.enexia.eventos.repositories.UsuarioRepository;
import com.enexia.eventos.utils.ValidadorPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Para evitar líos con el frontend de Vue más adelante
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    OrganizadorRepository organizadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Asegúrate de tener el Bean de BCrypt en tu SecurityConfig

    @Autowired
    private ValidadorPassword validadorPassword;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody RegistroDTO registroDTO) {
        try {
            // 1. OBTENER LOS DATOS DEL DTO
            String email = registroDTO.getEmail();
            String password = registroDTO.getPassword();

            // 2. VALIDACIÓN DE EXISTENCIA
            if (usuarioRepository.findByEmail(email) != null) {
                return ResponseEntity.badRequest().body("Error: El correo ya existe.");
            }

            // 3. VALIDACIÓN DE FORMATO DE CORREO
            if (email == null || !email.contains("@") || !email.endsWith(".com")) {
                return ResponseEntity.badRequest().body("Error: El correo debe ser válido y terminar en '.com'");
            }

            // 4. VALIDACIÓN CENTRALIZADA DE CONTRASEÑA (Nivel Backend)
            // Esto reemplaza todo el bloque de IFs, el bucle FOR y los chequeos de caracteres
            validadorPassword.validar(password);

            // 5. CIFRADO (Si la validación de arriba falla, esto nunca se ejecuta)
            String passCifrada = passwordEncoder.encode(password);

            // 6. ASIGNACIÓN POR ROL
            if ("ORGANIZADOR".equalsIgnoreCase(registroDTO.getRol())) {
                Organizador nuevoOrg = new Organizador();
                nuevoOrg.setNombre(registroDTO.getNombre());
                nuevoOrg.setApellido(registroDTO.getApellido());
                nuevoOrg.setEmail(email);
                nuevoOrg.setPassword(passCifrada);
                organizadorRepository.save(nuevoOrg);

            } else if ("PARTICIPANTE".equalsIgnoreCase(registroDTO.getRol())) {
                Participante nuevoPart = new Participante();
                nuevoPart.setNombre(registroDTO.getNombre());
                nuevoPart.setApellido(registroDTO.getApellido());
                nuevoPart.setEmail(email);
                nuevoPart.setPassword(passCifrada);
                participanteRepository.save(nuevoPart);

            } else {
                return ResponseEntity.badRequest().body("Error: Rol no válido.");
            }

            return ResponseEntity.ok("Registro exitoso como " + registroDTO.getRol().toUpperCase());

        } catch (Exception e) {
            // Acá se captura el mensaje específico del ValidadorPassword
            // Ejemplo: "Error: La contraseña debe incluir al menos una mayúscula..."
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}