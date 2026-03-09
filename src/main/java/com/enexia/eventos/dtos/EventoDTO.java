package com.enexia.eventos.dtos;

import com.enexia.eventos.models.Evento;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventoDTO {

    private Long id;

    private Long organizadorId;

    private List<InscripcionDTO> inscripciones;


    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer hora;
    private String categoria;
    private String direccion;
    private String descripcion;
    private Integer estado;
    private String urlImagen;

    public EventoDTO(Evento evento) {
        this.id = evento.getId();
        this.organizadorId = evento.getOrganizador().getID();
        this.urlImagen = evento.getUrlImagen(); // <--- MAPEAR AQUÍ
        this.nombre = evento.getNombre();
        this.fechaInicio = evento.getFechaInicio();
        this.fechaFin = evento.getFechaFin();
        this.hora = evento.getHora();
        this.categoria = evento.getCategoria();
        this.direccion = evento.getDireccion();
        this.descripcion = evento.getDescripcion();
        this.estado = evento.getEstado();
        // Mapeamos las inscripciones si las tiene
        this.inscripciones = evento.getInscripciones().stream()
                .map(InscripcionDTO::new)
                .collect(Collectors.toList());
    }


    public String getUrlImagen() {
        return urlImagen;
    }

    public Long getId() {
        return id;
    }

    public Long getOrganizadorId() {
        return organizadorId;
    }

    public List<InscripcionDTO> getInscripciones() {
        return inscripciones;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public Integer getHora() {
        return hora;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getEstado() {
        return estado;
    }
}
