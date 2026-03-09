package com.enexia.eventos.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableWebSecurity
@Configuration
public class WebAuthorization {

    // Inyectamos los componentes de WebAuthentication (UserDetailsService y PasswordEncoder)
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Componente que une el UserDetailsService y el PasswordEncoder ---
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    // --------------------------------------------------------------------------------------


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. Enlazar el Authentication Provider
        http.authenticationProvider(authenticationProvider());

        // 2. Reglas de Autorización (Ordenadas correctamente)
        http.authorizeRequests()


                // RUTAS PRIVADAS (ORGANIZADOR)

                .antMatchers("/web/O_panel.html").hasAuthority("ORGANIZADOR")
                .antMatchers("/web/O_crear_evento.html").hasAuthority("ORGANIZADOR")
                .antMatchers("/web/O_mis_eventos.html").hasAuthority("ORGANIZADOR")
                .antMatchers("/web/O_modificar_evento.html").hasAuthority("ORGANIZADOR")

                // RUTAS PARA AMBOS LOGUEADOS

                .antMatchers("/web/O_bienvenido.html").authenticated()

                // RUTAS PARTICIPANTE (PRIVADAS)


                .antMatchers("/web/mod_eventos.html").hasAuthority("PARTICIPANTE")
                .antMatchers("/web/mod_historial.html").hasAuthority("PARTICIPANTE")


                .antMatchers(HttpMethod.POST, "/api/inscribir/{eventoId}").hasAuthority("PARTICIPANTE")
                .antMatchers(HttpMethod.PUT, "/api/cancelar-inscripcion/{inscripcionId}").hasAuthority("PARTICIPANTE")

                .antMatchers(HttpMethod.GET, "/api/participante/historial").hasAuthority("PARTICIPANTE")


                // RUTAS PÚBLICAS



                .antMatchers("/", "/index.html", "/web/index.html", "/favicon.ico").permitAll()
                .antMatchers("/web/css/**", "/web/js/**", "/web/img/**").permitAll()


                .antMatchers(HttpMethod.GET, "/api/eventos/publico").permitAll()

                .antMatchers("/web/mod_cambiar_password.html").permitAll()
                .antMatchers("/web/recuperar_password.html").permitAll()



                .antMatchers(HttpMethod.GET, "/api/eventos/{id}").permitAll()
                .antMatchers(HttpMethod.POST, "/api/organizadores").permitAll()
                .antMatchers("/api/login", "/api/logout", "/api/registro/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/registrar").permitAll()
                .antMatchers(HttpMethod.GET, "/api/auth/test-email").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/recuperar-password").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/cambiar-password").permitAll()






                .antMatchers("/web/**").permitAll()

                // /APIS

                .antMatchers(HttpMethod.GET, "/api/eventos/mis-eventos").hasAuthority("ORGANIZADOR")
                .antMatchers(HttpMethod.POST, "/api/eventos").hasAuthority("ORGANIZADOR")
                .antMatchers(HttpMethod.PUT, "/api/eventos/**").hasAuthority("ORGANIZADOR")
                .antMatchers(HttpMethod.DELETE, "/api/eventos/**").hasAuthority("ORGANIZADOR")


                .antMatchers(HttpMethod.GET, "/api/usuarios/actual").authenticated()

                .anyRequest().permitAll();

        // 3. Configuración de Login (Encadenada para eliminar redundancia)
        http.formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginProcessingUrl("/api/login") // Usa loginProcessingUrl, no loginPage
                .successHandler((req, res, auth) -> clearAuthenticationAttributes(req))
                .failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // 4. Configuración de Logout (Encadenada para eliminar redundancia)
        http.logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

        // 5. Configuración de Filtros
        http.csrf().disable();
        http.headers().frameOptions().disable();

        // Añadir una sola vez el exceptionHandling
        http.exceptionHandling().authenticationEntryPoint(
                (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
        );

        return http.build();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {

            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        }
    }
}