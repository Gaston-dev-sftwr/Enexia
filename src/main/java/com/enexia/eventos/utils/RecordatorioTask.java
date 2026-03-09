package com.enexia.eventos.utils;

import com.enexia.eventos.models.*;
import com.enexia.eventos.repositories.*;
import com.enexia.eventos.services.ResendService; // Importamos el motor nuevo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RecordatorioTask {

    @Autowired
    private ResendService resendService; // Reemplazamos JavaMailSender por ResendService

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    // Se ejecuta según tu cron configurado
    @Scheduled(cron = "0 * * * * *")
    public void enviarRecordatorios() {
        // 1. Buscamos la fecha de mañana
        LocalDate manana = LocalDate.now().plusDays(1);

        // 2. Buscamos eventos que arranquen mañana
        List<Evento> eventosDeManana = eventoRepository.findByFechaInicio(manana);

        for (Evento evento : eventosDeManana) {
            // 3. Buscamos a los participantes anotados en ese evento que no recibieron recordatorio
            List<Inscripcion> inscripciones = inscripcionRepository.findByEventoAndRecordatorioEnviadoFalse(evento);

            for (Inscripcion insc : inscripciones) {
                Participante p = insc.getParticipante();

                // 4. Enviamos el mail usando la API de Resend
                enviarMail(p.getEmail(), p.getNombre(), evento.getNombre());

                // 5. Marcamos como enviado y guardamos en la base de datos de Aiven
                insc.setRecordatorioEnviado(true);
                inscripcionRepository.save(insc);

                System.out.println("✅ Recordatorio vía Resend enviado a: " + p.getEmail());
            }
        }
    }

    private void enviarMail(String destinatario, String nombre, String nombreEvento) {
        String asunto = "¡Mañana empieza tu evento: " + nombreEvento + "!";

        // Armamos un HTML lindo para el recordatorio
        String cuerpoHtml = String.format(
                "<div style='font-family: sans-serif; padding: 20px; border: 1px solid #eee; border-radius: 10px;'>" +
                        "<h2>¡Hola %s!</h2>" +
                        "<p>Este es un recordatorio de <strong>ENEXIA</strong>.</p>" +
                        "<p>Te esperamos mañana para el inicio del evento: <strong>%s</strong>.</p>" +
                        "<br><p>¡No te lo pierdas!</p>" +
                        "</div>",
                nombre, nombreEvento
        );

        // Llamamos al servicio que habla con la API (Puerto 443, sin bloqueos de Render)
        resendService.enviarCorreo(destinatario, asunto, cuerpoHtml);
    }
}