package com.enexia.eventos.configurations;

import com.enexia.eventos.models.Organizador;
import com.enexia.eventos.models.Usuario;
import com.enexia.eventos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class WebAuthentication extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    UsuarioRepository usuarioRepository;


    @Bean
    public UserDetailsService userDetailsService() {
        return inputName -> {
            Usuario usuario = usuarioRepository.findByEmail(inputName);

            if (usuario == null) {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }

            if (usuario instanceof Organizador) {

                return new User(usuario.getEmail(), usuario.getPassword(), AuthorityUtils.createAuthorityList("ORGANIZADOR"));
            } else {
                return new User(usuario.getEmail(), usuario.getPassword(),
                        AuthorityUtils.createAuthorityList("PARTICIPANTE"));
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}