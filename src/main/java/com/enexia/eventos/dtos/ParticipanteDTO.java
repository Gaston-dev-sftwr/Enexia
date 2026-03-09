package com.enexia.eventos.dtos;

import com.enexia.eventos.models.Inscripcion;
import com.enexia.eventos.models.Participante;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParticipanteDTO {

    private Long ID;

    private String nombre;

    private String apellido;

    private String email;

    private String password;

    private boolean estado;

    private List<InscripcionDTO> inscripciones;


    public ParticipanteDTO (Participante participante){

        this.ID = participante.getID();
        this.nombre = participante.getNombre();
        this.apellido = participante.getApellido();
        this.email = participante.getEmail();
        this.password = participante.getPassword();
        this.estado = participante.isEstado();

        this.inscripciones = participante.getInscripciones().stream().map(InscripcionDTO::new).collect(Collectors.toList());

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

    public List<InscripcionDTO> getInscripciones() {
        return inscripciones;
    }
}
