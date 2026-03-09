package com.enexia.eventos.utils;

import com.enexia.eventos.models.*;
import com.enexia.eventos.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RecordatorioTask {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    // Se ejecuta todos los días a las 8 AM
    @Scheduled(cron = "0 * * * * *")
    public void enviarRecordatorios() {
        // 1. Buscamos la fecha de mañana
        LocalDate manana = LocalDate.now().plusDays(1);

        // 2. Buscamos eventos que arranquen mañana
        List<Evento> eventosDeManana = eventoRepository.findByFechaInicio(manana);

        for (Evento evento : eventosDeManana) {
            // 3. Buscamos a los participantes anotados en ese evento
            List<Inscripcion> inscripciones = inscripcionRepository.findByEventoAndRecordatorioEnviadoFalse(evento);

            for (Inscripcion insc : inscripciones) {
                Participante p = insc.getParticipante();
                enviarMail(p.getEmail(), p.getNombre(), evento.getNombre());

                // MARCAMOS COMO ENVIADO Y GUARDAMOS EN MYSQL
                insc.setRecordatorioEnviado(true);
                inscripcionRepository.save(insc);

                System.out.println("Recordatorio enviado y registrado para: " + p.getEmail());
            }
        }
    }

    private void enviarMail(String destinatario, String nombre, String nombreEvento) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("notificaciones@enexia.com");
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Mañana empieza tu evento!");
        mensaje.setText("Hola " + nombre + ",\n\nTe recordamos que mañana inicia: " + nombreEvento);
        mailSender.send(mensaje);
    }
}