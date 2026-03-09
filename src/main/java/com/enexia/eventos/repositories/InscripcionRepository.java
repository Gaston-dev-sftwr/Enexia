package com.enexia.eventos.repositories;

import com.enexia.eventos.models.Evento;
import com.enexia.eventos.models.Inscripcion;
import com.enexia.eventos.models.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    List<Inscripcion> findByParticipanteID(Long participanteId);

    boolean existsByParticipanteAndEvento(Participante participante, Evento evento);

    List<Inscripcion> findByParticipanteIDAndEstado(Long participanteId, Integer estado);


    List<Inscripcion> findByEventoAndRecordatorioEnviadoFalse(Evento evento);
}
