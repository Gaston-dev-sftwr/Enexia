package com.enexia.eventos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Evento {

    /// Attributes ///

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_id_organizador")
    @JsonIgnoreProperties({"eventos", "password"})
    private Organizador organizador;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Inscripcion> inscripciones = new HashSet<>();

    private String urlImagen;

    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer hora;
    private String categoria;
    private String direccion;

    @Lob
    private String descripcion;
    private Integer estado;


    /// CONSTRUCTORS ///

    public Evento ( ) { }

    public Evento(String nombre, LocalDate fechaInicio, LocalDate fechaFin, Integer hora, String categoria, String direccion, String descripcion) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.hora = hora;
        this.categoria = categoria;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.estado = 1;
    }


    /// GETTERS ///

    public String getUrlImagen() {
        return urlImagen;
    }

    public Long getId() {
        return id;
    }

    public Organizador getOrganizador() {
        return organizador;
    }

    public Set<Inscripcion> getInscripciones() {
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



    /// SETTERS ///

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public void setOrganizador(Organizador organizador) {
        this.organizador = organizador;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setHora(Integer hora) {
        this.hora = hora;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public void addInscripcion(Inscripcion inscripcion){
        inscripcion.setEvento(this);
        this.inscripciones.add(inscripcion);
    }


}
