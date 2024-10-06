package com.ufps.maestria.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ufps.maestria.security.jwt.AuthEntryPointJwt;
import com.ufps.maestria.security.jwt.AuthTokenFilter;
import com.ufps.maestria.security.services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable()) // Ensure that CORS is correctly configured
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/api/v1/aspirante/crear",
                                "/api/v1/documentos/uploadFile/{tipoDocumento}",
                                "/api/v1/documentos/Aspirante/listar",
                                "/api/v1/documentos/crearDocs",
                                "/api/v1/documentos/uploadFile/{aspiranteId}/{tipoDocumento}").hasRole("USER")
                        .requestMatchers(
                                "/api/v1/aspirante/all",
                                "api/v1/aspirante/miPerfil/{aspiranteId}",

                                "api/v1/aspirante/cohorte/{cohorteId}",
                                "api/v1/aspirante/cohorte/{cohorteId}/historicos",
                                "api/v1/aspirante/{id}/usuario",
                                "api/v1/aspirante/disable",
                                "api/v1/aspirante/enable",
                                "api/v1/aspirante/byEmail",
                                "api/v1/aspirante/{aspiranteId}",
                                "api/v1/aspirante/cohorte/actual",
                                "api/v1/aspirante/habilitarFechaEntrevista",


                                "/api/v1/aspirante/aspirante/edit/{id}",
                                "/api/v1/aspirante/admin/delete/{id}",
                                "/api/v1/aspirante/cohorte/{cohorteId}",
                                "/api/v1/aspirante/cambiarEsEgresado",
                                "/api/v1/aspirante/rechazarAdmision",
                                "/api/v1/aspirante/{aspiranteId}/calificaciones",
                                "/api/v1/aspirante/{aspiranteId}/admitir",
                                "/api/v1/aspirante/calificarPrueba",
                                "/api/v1/aspirante/habilitarFechaEntrevista",
                                "/api/v1/aspirante/disable",
                                "/api/v1/aspirante/activar"
                        ).hasAnyRole("ADMIN", "MODERATOR")
                        .requestMatchers(
                                "/api/admin",
                                "/api/admin/create-user",
                                "/api/admin/edit/{id}",
                                "/api/admin/delete/{id}",
                                "/api/admin/users",
                                "/api/admin/aspirantes",
                                "/api/admin/logout",
                                "/api/admin/obtenerAspirantesHistoricos",
                                "/api/admin/delete/{id}"
                        ).hasAnyRole("ADMIN", "MODERATOR")
                        .requestMatchers(
                                "/api/v1/cohorte/create",
                                "/api/v1/cohorte/all",
                                "/api/v1/cohorte/cerrar",
                                "/api/v1/cohorte/fechaFin",
                                "/api/v1/cohorte/abierto",
                                "/api/v1/cohorte/entrevistaEnlace",
                                "/api/v1/cohorte/prueba",
                                "/api/v1/cohorte/eliminar/{id}",
                                "/api/v1/cohorte/editar/{id}",
                                "/api/v1/cohorte/obtenerPorId/{id}}",
                                "/api/v1/documentosEstados/aprobar/{documentoId}/{aspiranteId}",
                                "/api/v1/documentosEstados/retroalimentacion",
                                "/api/v1/documentosEstados/filtrar",
                                "/api/v1/documentosEstados/Aspirantes/listarDoc"
                        ).hasAnyRole("ADMIN", "MODERATOR")
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
