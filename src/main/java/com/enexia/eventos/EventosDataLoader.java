package com.enexia.eventos;

import com.enexia.eventos.models.Evento;
import com.enexia.eventos.models.Organizador;
import com.enexia.eventos.repositories.EventoRepository;
import com.enexia.eventos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder; // Necesario

import java.time.LocalDate;

@Configuration
public class EventosDataLoader {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Aseguramos la inyección del codificador

    @Bean
    public CommandLineRunner initData() {
        return (args) -> {
            System.out.println("Cargando datos iniciales de prueba...");

            // 1. Obtener/Crear el Organizador de prueba
            Organizador organizadorPrueba = (Organizador) usuarioRepository.findByEmail("tester@eventos.com");

            // Si el usuario NO existe
            if (organizadorPrueba == null) {
                // Generamos el hash SÍ O SÍ con el codificador inyectado
                String encodedPassword = passwordEncoder.encode("password");

                organizadorPrueba = new Organizador(
                        "Admin",
                        "Pruebas",
                        "tester@eventos.com",
                        encodedPassword // Usamos el hash generado
                );
                usuarioRepository.save(organizadorPrueba);
                System.out.println("Organizador de prueba creado: tester@eventos.com / password (codificado)");
            }

            // --- Carga de Eventos (Solo si no existen) ---

            // EVENTO 1: Ushuaia EPIC
            if (eventoRepository.findByNombre("Ushuaia EPIC – MTB en el Fin del Mundo").isEmpty()) {
                Evento ushuaiaEpic = new Evento();
                ushuaiaEpic.setNombre("Ushuaia EPIC – MTB en el Fin del Mundo");
                ushuaiaEpic.setUrlImagen("http://localhost:8081/web/img/flyer-ushuia-epic-mtb-2025.jpg");
                ushuaiaEpic.setFechaInicio(LocalDate.of(2025, 11, 22));
                ushuaiaEpic.setFechaFin(LocalDate.of(2025, 11, 23));
                ushuaiaEpic.setHora(10);
                ushuaiaEpic.setCategoria("Deportes: Mountain Bike (MTB)");
                ushuaiaEpic.setDireccion("Polideportivo Municipal, Ushuaia, Tierra del Fuego");
                ushuaiaEpic.setDescripcion("USHUAIA EPIC – MTB en el Fin del Mundo, es una competencia de ciclismo de montaña exigente y de renombre nacional e internacional. Se compite en un circuito exclusivo en parejas por senderos técnicos de montaña próximos a la ciudad de Ushuaia, poniendo a prueba la resistencia y habilidad técnica de los participantes.");
                ushuaiaEpic.setEstado(1);
                ushuaiaEpic.setOrganizador(organizadorPrueba);
                eventoRepository.save(ushuaiaEpic);
                System.out.println("Evento 'Ushuaia EPIC' cargado.");
            }

            // EVENTO 2: Bajada con Antorchas
            if (eventoRepository.findByNombre("Bajada con Antorchas del Glaciar Martial").isEmpty()) {
                Evento bajadaAntorchas = new Evento();
                bajadaAntorchas.setNombre("Bajada con Antorchas del Glaciar Martial");
                bajadaAntorchas.setUrlImagen("http://localhost:8081/web/img/antorcha.jpg");
                bajadaAntorchas.setFechaInicio(LocalDate.of(2026, 8, 9));
                bajadaAntorchas.setFechaFin(LocalDate.of(2026, 8, 9));
                bajadaAntorchas.setHora(13);
                bajadaAntorchas.setCategoria("Deportes/Social: Espectáculo de Invierno");
                bajadaAntorchas.setDireccion("Base del Glaciar Martial, Ushuaia");
                bajadaAntorchas.setDescripcion("La tradicional Bajada con Antorchas del Glaciar Martial es un evento que celebra el invierno fueguino. Incluye música en vivo, degustaciones y un descenso espectacular con antorchas por 70/80 esquiadores. La cita es a partir de las 13h.");
                bajadaAntorchas.setEstado(1);
                bajadaAntorchas.setOrganizador(organizadorPrueba);
                eventoRepository.save(bajadaAntorchas);
                System.out.println("Evento 'Bajada con Antorchas' cargado.");
            }
        };
    }
}