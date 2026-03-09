package com.enexia.eventos.controllers;

import com.enexia.eventos.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;

import com.enexia.eventos.dtos.InscripcionDTO;
import com.enexia.eventos.models.Evento;
import com.enexia.eventos.models.Inscripcion;
import com.enexia.eventos.models.Participante;
import com.enexia.eventos.models.Usuario;
import com.enexia.eventos.repositories.EventoRepository;
import com.enexia.eventos.repositories.InscripcionRepository;
import com.enexia.eventos.repositories.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ParticipanteController {


    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;



    @PostMapping("/inscribir/{eventoId}")
    public ResponseEntity<?> inscribir(@PathVariable Long eventoId, Authentication authentication) {
        try {
            // 1. OBTENER EL USUARIO DE LA SESIÓN (Seguridad)
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            Usuario usuario = usuarioRepository.findByEmail(email);

            if (!(usuario instanceof Participante)) {
                return new ResponseEntity<>("Solo los participantes pueden inscribirse.", HttpStatus.FORBIDDEN);
            }

            Participante participante = (Participante) usuario;

            // 2. BUSCAR EL EVENTO
            Evento evento = eventoRepository.findById(eventoId).orElse(null);
            if (evento == null) {
                return ResponseEntity.badRequest().body("Error: El evento no existe.");
            }

            // --- 3. VALIDACIÓN DE PASO: EVITAR DUPLICADOS ---
            boolean yaInscrito = inscripcionRepository.existsByParticipanteAndEvento(participante, evento);
            if (yaInscrito) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Atención: Ya te encuentras inscrito en el evento '" + evento.getNombre() + "'.");
            }

            // 4. CREAR Y CONFIGURAR LA ENTIDAD
            Inscripcion nuevaInscripcion = new Inscripcion();
            nuevaInscripcion.setParticipante(participante);
            nuevaInscripcion.setEvento(evento);
            nuevaInscripcion.setFechaInscripcion(LocalDate.now());
            nuevaInscripcion.setEstado(1);

            // 5. GUARDAR EN LA DB
            inscripcionRepository.save(nuevaInscripcion);

            return ResponseEntity.ok("¡Inscripción exitosa! Te has anotado al evento: " + evento.getNombre());

        } catch (Exception e) {
            return new ResponseEntity<>("Error técnico: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/cancelar-inscripcion/{inscripcionId}")
    public ResponseEntity<?> cancelar(@PathVariable Long inscripcionId, Authentication authentication) {
        try {
            // 1. Obtener el participante desde la sesión (Seguridad)
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            Usuario usuario = usuarioRepository.findByEmail(email);

            if (!(usuario instanceof Participante)) {
                return new ResponseEntity<>("Acceso denegado.", HttpStatus.FORBIDDEN);
            }
            Participante participanteActual = (Participante) usuario;

            // 2. Buscar la inscripción
            Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId).orElse(null);

            if (inscripcion == null) {
                return ResponseEntity.badRequest().body("Error: No se encontró la inscripción.");
            }

            // 3. VALIDACIÓN DE SEGURIDAD REAL:
            // Verificamos que la inscripción le pertenezca al que está pidiendo cancelarla
            if (!inscripcion.getParticipante().getID().equals(participanteActual.getID())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Error: No tienes permiso para cancelar inscripciones ajenas.");
            }

            // 4. Borrado lógico
            inscripcion.setEstado(0);
            inscripcionRepository.save(inscripcion);

            return ResponseEntity.ok("Inscripción cancelada correctamente.");

        } catch (Exception e) {
            return new ResponseEntity<>("Error al cancelar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/participante/historial")
    public ResponseEntity<?> verHistorial(Authentication authentication) {
        try {
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            Usuario usuario = usuarioRepository.findByEmail(email);

            if (!(usuario instanceof Participante)) {
                return new ResponseEntity<>("Solo los participantes tienen historial.", HttpStatus.FORBIDDEN);
            }
            Participante participante = (Participante) usuario;

            // Buscamos inscripciones con estado 1 (Activas)
            List<Inscripcion> historialEntidades = inscripcionRepository.findByParticipanteIDAndEstado(participante.getID(), 1);

            // IMPORTANTE: Si está vacío, devolvemos una lista vacía, NO un mensaje de texto
            if (historialEntidades.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            List<InscripcionDTO> historialDTO = historialEntidades.stream()
                    .map(InscripcionDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(historialDTO);

        } catch (Exception e) {
            return new ResponseEntity<>("Error al cargar el historial: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
