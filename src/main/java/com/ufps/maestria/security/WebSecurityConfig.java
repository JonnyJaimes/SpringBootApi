package com.bezkoder.springjwt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bezkoder.springjwt.security.jwt.AuthEntryPointJwt;
import com.bezkoder.springjwt.security.jwt.AuthTokenFilter;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity
// (securedEnabled = true,
// jsr250Enabled = true,
// prePostEnabled = true) // by default
public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

//  @Override
//  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//  }
  
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
   
      return authProvider;
  }

//  @Bean
//  @Override
//  public AuthenticationManager authenticationManagerBean() throws Exception {
//    return super.authenticationManagerBean();
//  }
  
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

//  @Override
//  protected void configure(HttpSecurity http) throws Exception {
//    http.cors().and().csrf().disable()
//      .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//      .authorizeRequests().antMatchers("/api/auth/**").permitAll()
//      .antMatchers("/api/test/**").permitAll()
//      .anyRequest().authenticated();
//
//    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//  }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // Authentication endpoints
                        .requestMatchers("/api/test/**").permitAll()  // Public test endpoints
                        .requestMatchers("/api/v1/aspirante", "/api/v1/documentos/uploadFile/{tipoDocumento}","/api/v1/documentos/uploadFile/{aspiranteId}/{tipoDocumento}").hasRole("USER")  // Endpoints accessible by USER role
                        .requestMatchers(
                                "/api/v1/aspirante/all",
                                "/api/v1/aspirante/aspirante/edit/{id}",
                                "/api/v1/aspirante/admin/delete/{id}",
                                "/api/v1/aspirante/cohorte/{cohorteId}",
                                "/api/v1/aspirante/cambiarEsEgresado",
                                "/api/v1/aspirante/rechazarAdmision",
                                "/api/v1/aspirante/{aspiranteId}/calificaciones",
                                "/api/v1/aspirante/{aspiranteId}/admitir",
                                "/api/v1/aspirante/calificarPrueba",
                                "/api/v1/aspirante/habilitarFechaEntrevista",
                                "/api/v1/aspirante/desactivar",
                                "/api/v1/aspirante/activar"
                        ).hasAnyRole("ADMIN", "MODERATOR")  // Endpoints accessible by ADMIN and MODERATOR
                        .requestMatchers(
                                "/api/admin",
                                "/api/admin/edit/{id}",
                                "/api/admin/delete/{id}",
                                "/api/admin/users",
                                "/api/admin/aspirantes",
                                "/api/admin/logout",
                                "/api/admin/obtenerAspirantesHistoricos"
                        ).hasAnyRole("ADMIN", "MODERATOR")  // Admin and Moderator specific endpoints
                        .requestMatchers(
                                "/api/v1/cohorte/**",
                                "/api/v1/documentosEstados/**"
                        ).hasAnyRole("ADMIN", "MODERATOR")  // Include new controller endpoints
                        .anyRequest().authenticated()  // All other endpoints require authentication
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
