package com.enexia.eventos.dtos;

import com.enexia.eventos.models.Inscripcion;
import java.time.format.DateTimeFormatter;

public class InscripcionDTO {

    private Long id;
    private Long participanteId;
    private Long eventoId;
    private Integer estado;

    // CAMPOS NECESARIOS PARA EL FRONTEND:
    private String eventoNombre;
    private String urlImagenEvento;
    private String fechaInscripcion;
    private String direccion;
    private String nombreOrganizador;

    public InscripcionDTO (Inscripcion inscripcion) {
        this.id = inscripcion.getId();
        this.participanteId = inscripcion.getParticipante().getID();
        this.estado = inscripcion.getEstado();

        // Extraemos los datos del objeto Evento relacionado
        if (inscripcion.getEvento() != null) {
            this.eventoId = inscripcion.getEvento().getId();
            this.eventoNombre = inscripcion.getEvento().getNombre();
            this.urlImagenEvento = inscripcion.getEvento().getUrlImagen();
            this.direccion = inscripcion.getEvento().getDireccion();

            // Si el evento tiene organizador, ya lo dejamos mapeado
            if (inscripcion.getEvento().getOrganizador() != null) {
                this.nombreOrganizador = inscripcion.getEvento().getOrganizador().getNombre();
            }
        }

        if (inscripcion.getFechaInscripcion() != null) {
            // Formateamos la fecha para que sea legible
            this.fechaInscripcion = inscripcion.getFechaInscripcion().toString();
        }
    }

    // Getters (Asegúrate de tener todos para que Jackson pueda serializar el JSON)
    public Long getId() { return id; }
    public Long getParticipanteId() { return participanteId; }
    public Long getEventoId() { return eventoId; }
    public Integer getEstado() { return estado; }
    public String getEventoNombre() { return eventoNombre; }
    public String getUrlImagenEvento() { return urlImagenEvento; }
    public String getFechaInscripcion() { return fechaInscripcion; }
    public String getDireccion() { return direccion; }
    public String getNombreOrganizador() { return nombreOrganizador; }
}