

package com.enexia.eventos.controllers;

import com.enexia.eventos.models.Evento;
import com.enexia.eventos.models.Organizador;
import com.enexia.eventos.models.Usuario;
import com.enexia.eventos.repositories.OrganizadorRepository;
import com.enexia.eventos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class OrganizadorController {

    @Autowired
    private OrganizadorRepository organizadorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/organizadores")
    public List<Organizador> getOrganizadores() {
        return organizadorRepository.findAll();
    }

    @PostMapping("/organizadores")
    public ResponseEntity<Object> registrarOrganizador(@RequestBody Organizador organizador) {

        Usuario usuarioExistente = usuarioRepository.findByEmail(organizador.getEmail());

        if (usuarioExistente != null) {
            // Regla de Negocio: Email debe ser único.
            return new ResponseEntity<>("El email ya se encuentra registrado.", HttpStatus.FORBIDDEN); // Cambiar a 409
        }

        String encodedPassword = passwordEncoder.encode(organizador.getPassword());
        organizador.setPassword(encodedPassword);

        organizadorRepository.save(organizador);

        return new ResponseEntity<>("Registro de Organizador exitoso.", HttpStatus.CREATED); // 201
    }
}