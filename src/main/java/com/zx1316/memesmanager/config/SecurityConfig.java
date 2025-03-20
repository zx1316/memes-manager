package com.zx1316.memesmanager.config;

import com.zx1316.memesmanager.component.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)  // 禁用CSRF保护
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 设置为无状态会话
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.PUT, "/api/v1/memes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/memes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/memes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/memes").hasRole("ADMIN")
                        .requestMatchers("/api/v1/memes/fuzzy-filter").authenticated()
                        .requestMatchers("/api/v1/memes/filter").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)     // 禁掉大部分东西
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
