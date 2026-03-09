package com.enexia.eventos.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.enexia.eventos.dtos.EventoDTO;
import com.enexia.eventos.models.Evento;
import com.enexia.eventos.models.Organizador;
import com.enexia.eventos.models.Usuario;
import com.enexia.eventos.repositories.EventoRepository;
import com.enexia.eventos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Público: Obtener TODOS los eventos

    @GetMapping("/eventos/publico")
    public List<EventoDTO> getAllEventosPublico() {
        // 1. Buscamos todas las entidades
        List<Evento> eventos = eventoRepository.findAll();

        // 2. Las convertimos a DTO usando el constructor que creamos
        return eventos.stream()
                .map(evento -> new EventoDTO(evento))
                .collect(Collectors.toList());
    }

    @GetMapping("/eventos/{id}")
    public ResponseEntity<?> getEvento(@PathVariable Long id){
        return eventoRepository.findById(id)
                .map(evento -> ResponseEntity.ok(new EventoDTO(evento))) // Convertimos a DTO aquí
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener los eventos del organizador autenticado
    @GetMapping("/eventos/mis-eventos")
    public List<EventoDTO> getMisEventos(Authentication authentication) {

        String organizadorEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Usuario usuario = usuarioRepository.findByEmail(organizadorEmail);

        if (usuario instanceof Organizador) {
            Organizador organizador = (Organizador) usuario;

            // 1. Obtenemos la lista de entidades de la base de datos (List<Evento>)
            List<Evento> misEventos = eventoRepository.findByOrganizadorIDAndEstado(organizador.getID(), 1);

            // 2. Transformamos cada Evento en un EventoDTO usando el constructor que creamos
            return misEventos.stream()
                    .map(evento -> new EventoDTO(evento)) // Esto usa el constructor que ya tenés
                    .collect(Collectors.toList());        // Lo volvemos a agrupar en una lista
        }

        // Si no es organizador, devolvemos una lista vacía de DTOs
        return List.of();
    }

    @Autowired
    private Cloudinary cloudinary; // Inyectamos Cloudinary

    // Crear un nuevo evento con imagen
    @PostMapping("/eventos")
    public ResponseEntity<?> crearEvento(
            @RequestParam("nombre") String nombre,
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin,
            @RequestParam("hora") Integer hora,
            @RequestParam("categoria") String categoria,
            @RequestParam("direccion") String direccion,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("file") MultipartFile file, // Recibimos el archivo
            Authentication authentication) {

        try {
            // 1. Lógica de Seguridad (La que ya tenías)
            String organizadorEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
            Usuario usuario = usuarioRepository.findByEmail(organizadorEmail);

            if (!(usuario instanceof Organizador)) {
                return new ResponseEntity<>("Solo los organizadores pueden crear eventos.", HttpStatus.FORBIDDEN);
            }

            // 2. Subir imagen a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String urlImagen = uploadResult.get("secure_url").toString();

            // 3. Crear y configurar la entidad Evento
            Evento evento = new Evento();
            evento.setNombre(nombre);
            evento.setFechaInicio(LocalDate.parse(fechaInicio));
            evento.setFechaFin(LocalDate.parse(fechaFin));
            evento.setHora(hora);
            evento.setCategoria(categoria);
            evento.setDireccion(direccion);
            evento.setDescripcion(descripcion);
            evento.setUrlImagen(urlImagen); // Guardamos el link de la nube
            evento.setOrganizador((Organizador) usuario);
            evento.setEstado(1);

            // 4. Guardar en MySQL
            Evento nuevoEvento = eventoRepository.save(evento);
            return new ResponseEntity<>(nuevoEvento, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Error al procesar el evento: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Modificar un evento
    @PutMapping("/eventos/{id}")
    public ResponseEntity<?> modificarEvento(@PathVariable Long id, @RequestBody Evento eventoActualizado, Authentication authentication) {

        Optional<Evento> eventoExistenteOpt = eventoRepository.findById(id);

        if (eventoExistenteOpt.isEmpty()) {
            return new ResponseEntity<>("Evento no encontrado.", HttpStatus.NOT_FOUND);
        }

        Evento eventoExistente = eventoExistenteOpt.get();

        String organizadorEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        if (!eventoExistente.getOrganizador().getEmail().equals(organizadorEmail)) {
            return new ResponseEntity<>("No tienes permiso para modificar este evento.", HttpStatus.FORBIDDEN);
        }

        eventoExistente.setNombre(eventoActualizado.getNombre());
        eventoExistente.setFechaInicio(eventoActualizado.getFechaInicio());
        eventoExistente.setFechaFin(eventoActualizado.getFechaFin());
        eventoExistente.setHora(eventoActualizado.getHora());
        eventoExistente.setCategoria(eventoActualizado.getCategoria());
        eventoExistente.setDireccion(eventoActualizado.getDireccion());
        eventoExistente.setDescripcion(eventoActualizado.getDescripcion());
        eventoExistente.setEstado(eventoActualizado.getEstado());

        Evento eventoGuardado = eventoRepository.save(eventoExistente);
        return new ResponseEntity<>(eventoGuardado, HttpStatus.OK);
    }

    // --- D (Delete): Eliminar lógicamente un evento (Privado y Seguro) ---
    @DeleteMapping("/eventos/{id}")
    public ResponseEntity<?> eliminarEvento(@PathVariable Long id, Authentication authentication) {
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isEmpty()) {
            return new ResponseEntity<>("Evento no encontrado.", HttpStatus.NOT_FOUND);
        }
        Evento evento = eventoOpt.get();

        String organizadorEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        if (!evento.getOrganizador().getEmail().equals(organizadorEmail)) {
            return new ResponseEntity<>("No tienes permiso para eliminar este evento.", HttpStatus.FORBIDDEN);
        }

        evento.setEstado(0);
        eventoRepository.save(evento);
        return new ResponseEntity<>("Evento 'desactivado' exitosamente.", HttpStatus.OK);
    }
}
