package com.enexia.eventos.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Organizador extends Usuario {

    @JsonIgnoreProperties("organizador")
    @OneToMany(mappedBy = "organizador", fetch = FetchType.LAZY)
    private Set<Evento> eventos = new HashSet<>();

    public Organizador() { this.setEstado(true); }

    public Organizador ( String nombre, String apellido, String email, String password ) {
        super(nombre, apellido, email, password);
    }

    public Set<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(Evento evento) {
        this.eventos.add(evento);
        evento.setOrganizador(this);
    }
}