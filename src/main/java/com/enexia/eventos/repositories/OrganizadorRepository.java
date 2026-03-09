package com.enexia.eventos.repositories;

import com.enexia.eventos.models.Organizador;
import com.enexia.eventos.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizadorRepository extends JpaRepository<Organizador, Long> {

    Organizador findByEmail(String email);

}
