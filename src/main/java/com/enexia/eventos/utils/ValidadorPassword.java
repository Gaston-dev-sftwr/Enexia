package com.enexia.eventos.utils;

import org.springframework.stereotype.Component;

@Component
public class ValidadorPassword {

    public void validar(String password) throws Exception {
        // Requisito A: Largo mínimo de 8 caracteres
        if (password == null || password.length() < 8) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres.");
        }

        boolean tieneMayuscula = false;
        boolean tieneNumero = false;
        boolean tieneEspecial = false;
        String caracteresEspeciales = "!@#$%^&*()-_=+";

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                tieneMayuscula = true;
            } else if (Character.isDigit(c)) {
                tieneNumero = true;
            } else if (caracteresEspeciales.contains(String.valueOf(c))) {
                tieneEspecial = true;
            }
        }

        // Verificamos si cumplió con todos los requisitos
        if (!tieneMayuscula || !tieneNumero || !tieneEspecial) {
            throw new Exception("La contraseña debe incluir al menos una mayúscula, un número y un carácter especial (" + caracteresEspeciales + ")");
        }
    }
}