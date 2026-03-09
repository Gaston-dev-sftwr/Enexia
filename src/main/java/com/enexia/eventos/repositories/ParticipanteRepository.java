package com.enexia.eventos.repositories;

import com.enexia.eventos.models.Participante;
import com.enexia.eventos.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

}
