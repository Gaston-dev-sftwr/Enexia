package com.enexia.eventos.controllers;

import com.enexia.eventos.models.Organizador;
import com.enexia.eventos.models.Usuario;
import com.enexia.eventos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class

UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/actual")
    public ResponseEntity<Map<String, String>> getUsuarioActual(Authentication authentication) {

        // 1. Obtenemos el email de la sesión activa
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Preparamos el mapa de respuesta
        Map<String, String> userData = new HashMap<>();
        userData.put("nombre", usuario.getNombre());
        userData.put("apellido", usuario.getApellido());

        // 3. LÓGICA DE ROL: Detectamos si es Organizador o Participante
        String rol = "PARTICIPANTE"; // Valor por defecto

        if (usuario instanceof Organizador) {
            rol = "ORGANIZADOR";
        }

        userData.put("rol", rol); // Enviamos el rol al frontend

        return ResponseEntity.ok(userData);
    }


}
