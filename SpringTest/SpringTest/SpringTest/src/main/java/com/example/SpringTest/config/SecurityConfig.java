package com.example.SpringTest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/books/**", "/actuator/**" ,"/test/**").permitAll()  // This allows all book endpoints
                .anyRequest().authenticated()
                .and()
                .httpBasic();

        return http.build();
    }
}