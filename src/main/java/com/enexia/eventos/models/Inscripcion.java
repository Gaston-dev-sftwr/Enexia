package com.enexia.eventos.models;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Inscripcion {


    /// ATTRIBUTES ///

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "participante_id")
    private Participante participante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @Column(name = "recordatorio_enviado")
    private boolean recordatorioEnviado = false;


    private LocalDate fechaInscripcion;
    private Integer estado;


    /// CONSTRUCTORS ///

    public Inscripcion ( ) { }

    public Inscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
        this.estado = 1;
    }


    /// GETTERS ///

    public Long getId() {
        return id;
    }

    public Participante getParticipante() {
        return participante;
    }

    public Evento getEvento() {
        return evento;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public Integer getEstado() {
        return estado;
    }


    /// SETTERS ///

    public void setParticipante(Participante participante) {
        this.participante = participante;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public boolean isRecordatorioEnviado() { return recordatorioEnviado; }
    public void setRecordatorioEnviado(boolean recordatorioEnviado) { this.recordatorioEnviado = recordatorioEnviado; }
}
