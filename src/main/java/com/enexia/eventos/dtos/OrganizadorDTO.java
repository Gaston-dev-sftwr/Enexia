package com.enexia.eventos.dtos;

import com.enexia.eventos.models.Evento;
import com.enexia.eventos.models.Organizador;

import java.util.Set;

public class OrganizadorDTO {


    private Long ID;

    private String nombre;

    private String apellido;

    private String email;

    private String password;

    private boolean estado;


    private Set<Evento> eventos;


    public OrganizadorDTO (Organizador organizador) {

        this.ID = organizador.getID();
        this.nombre = organizador.getNombre();
        this.apellido = organizador.getApellido();
        this.email = organizador.getEmail();
        this.password = organizador.getPassword();
        this.estado = organizador.isEstado();

        this.eventos = organizador.getEventos();


    }

    public Long getID() {
        return ID;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEstado() {
        return estado;
    }

    public Set<Evento> getEventos() {
        return eventos;
    }
}
