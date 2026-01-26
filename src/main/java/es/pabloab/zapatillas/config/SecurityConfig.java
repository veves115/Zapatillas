package es.pabloab.zapatillas.config;

import es.pabloab.zapatillas.rest.auth.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de Spring Security.
 * 
 * Esta clase configura:
 * 1. Las reglas de autorización a nivel de URL (qué endpoints son públicos)
 * 2. El filtro JWT para autenticación
 * 3. La política de sesiones (STATELESS para APIs REST)
 * 4. El proveedor de autenticación
 * 
 * IMPORTANTE: 
 * - @EnableMethodSecurity habilita las anotaciones @PreAuthorize en los controladores
 * - Las reglas de autorización específicas (por rol) se definen en los controladores
 *   usando @PreAuthorize, no aquí en SecurityConfig
 * - Este SecurityConfig solo define qué endpoints son públicos (permitAll)
 *   y qué endpoints requieren autenticación (authenticated)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita @PreAuthorize y @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Configura la cadena de filtros de seguridad.
     * 
     * Explicación de las reglas:
     * - permitAll(): Endpoints públicos, no requieren autenticación
     * - authenticated(): Endpoints que requieren autenticación (pero cualquier rol)
     * - Las reglas específicas por rol se manejan con @PreAuthorize en los controladores
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitado para APIs REST (usamos JWT)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (no requieren autenticación)
                        .requestMatchers("/api/v1/auth/**").permitAll() // Login y registro
                        .requestMatchers("/h2-console/**").permitAll() // Consola H2 (solo desarrollo)
                        .requestMatchers("/ws/**").permitAll() // WebSockets
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // Swagger
                        .requestMatchers("/", "/index", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll() // Recursos estáticos
                        // Todos los demás endpoints requieren autenticación
                        // Las reglas específicas por rol se definen con @PreAuthorize en los controladores
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        // STATELESS: No guardamos sesiones (usamos JWT)
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                // Añadimos el filtro JWT antes del filtro de autenticación por defecto
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); // Para H2 console

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setUserDetailsPasswordService((UserDetailsPasswordService) userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
