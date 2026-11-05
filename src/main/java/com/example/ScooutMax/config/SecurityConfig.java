package com.example.ScooutMax.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration 
@EnableWebSecurity 
public class SecurityConfig {
    // Uma API REST sem estado (ou stateless) é uma arquitetura onde cada solicitação do cliente ao servidor
    //  é totalmente independente e contém todas as informações
    //  necessárias para seu processamento.  O servidor não armazena nenhum contexto
    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desativa CSRF (necessário para POST externo/Swagger/Curl)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // API REST sem estado
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/scouting/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Libera explicitamente as suas rotas
                .anyRequest().permitAll() // Libera qualquer outra rota por precaução
            );
            
        return http.build();
    }
}