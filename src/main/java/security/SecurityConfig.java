package com.nexowear.Nexowear.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Definimos el Encriptador como un Bean para solucionar la línea roja de tu UsuarioService
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Definimos nuestro filtro interceptor de JWT
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // 3. El manejador de autenticaciones nativo de Spring
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 4. El cerebro: Aquí configuramos los accesos permitidos y los protegidos
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitamos CSRF porque usaremos tokens JWT en la API
                .csrf(csrf -> csrf.disable())

                // Indicamos que la API no guardará estados de sesión (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configuración de las reglas de las URLs
                .authorizeHttpRequests(auth -> auth
                        // 1. Permitir peticiones de Preflight (OPTIONS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Rutas públicas de la Web (Vistas de Thymeleaf, imágenes y estilos)
                        .requestMatchers("/", "/productos/**", "/contacto/**", "/css/**", "/img/**", "/js/**", "/admin/dashboard").permitAll()

                        // 3. Endpoints públicos de Autenticación (Login y Registro)
                        .requestMatchers("/auth/**").permitAll()

                        // 4. Endpoints protegidos del Carrito (Para cualquier usuario logueado)
                        .requestMatchers("/api/carrito/**").authenticated()

                        // 5. 🌟 PROTECCIÓN ESTRICTA DEL PANEL: Solo usuarios con Rol ADMIN pueden hacer POST o DELETE
                        .requestMatchers("/admin/productos/**").hasRole("ADMIN")

                        .requestMatchers("/admin/productos/**").permitAll()
                        // Cualquier otra petición requerirá autenticación
                        .anyRequest().authenticated()
                );

        // Conectamos nuestro filtro guardián antes de que Spring ejecute su verificación por defecto
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}