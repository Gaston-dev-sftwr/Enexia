package com.enexia.eventos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Participante extends Usuario {

    @OneToMany(mappedBy = "participante", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Inscripcion> inscripciones = new HashSet<>();

    public Participante ( ) { this.setEstado(true); }

    public Participante ( String nombre, String apellido, String email, String password ) {
        super(nombre, apellido, email, password);
    }

    ///  ERror message y toats

    public Set<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(Inscripcion inscripcion) {
        this.inscripciones.add(inscripcion);
        inscripcion.setParticipante(this);
    }
}