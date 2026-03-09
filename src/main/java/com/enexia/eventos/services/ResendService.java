package com.enexia.eventos.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResendService {

    private final String apiKey = System.getenv("RESEND_API_KEY");

    public void enviarCorreo(String destinatario, String asunto, String cuerpoHtml) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.resend.com/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        // En Resend gratis, el 'from' es fijo
        body.put("from", "Enexia <onboarding@resend.dev>");
        body.put("to", destinatario);
        body.put("subject", asunto);
        body.put("html", cuerpoHtml);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }
}