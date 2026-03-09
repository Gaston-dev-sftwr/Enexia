package com.enexia.eventos.repositories;

import com.enexia.eventos.models.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByOrganizadorID(Long organizadorId);

    Optional<Evento> findByNombre(String nombre);

    List<Evento> findByOrganizadorIDAndEstado(Long organizadorId, Integer estado);


    List<Evento> findByFechaInicio(LocalDate manana);
}
