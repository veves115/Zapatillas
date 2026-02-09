package es.pabloab.zapatillas.config;

import es.pabloab.zapatillas.rest.auth.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
 * REGLAS DE ACCESO:
 *
 * PÚBLICAS (sin autenticación):
 * - GET /api/v1/zapatillas/** - Todo el mundo puede ver zapatillas
 * - POST /api/v1/auth/** - Login y registro
 * - /h2-console/** - Consola H2 (solo desarrollo)
 * - /ws/** - WebSockets
 * - /swagger-ui/** - Documentación API
 * - Recursos estáticos (/, /index, /webjars/**, /css/**, etc.)
 *
 * REQUIEREN AUTENTICACIÓN:
 * - POST, PUT, PATCH, DELETE /api/v1/zapatillas/** - Solo ADMIN (vía @PreAuthorize)
 * - /api/v1/usuarios/** - ADMIN puede todo, USER solo sus propios clientes
 * - /api/v1/users/** - ADMIN puede todo, USER solo su propio perfil
 *
 * IMPORTANTE:
 * - @EnableMethodSecurity habilita @PreAuthorize en controladores
 * - Las reglas específicas por rol se definen en los controladores con @PreAuthorize
 * - Este SecurityConfig solo define qué es público vs requiere autenticación
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
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
                        .requestMatchers("/api/v1/auth/**","/api/*/auth/**").permitAll() // Login y registro
                        .requestMatchers(HttpMethod.GET,"/api/v1/zapatillas/**").permitAll()
                        .requestMatchers("/graphql/**").permitAll() // Endpoint GraphQL
                        .requestMatchers("/graphiql/**").permitAll() // Playground GraphiQL
                        .requestMatchers("/h2-console/**").permitAll() // Consola H2 (solo desarrollo)
                        .requestMatchers("/ws/**").permitAll() // WebSockets
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // Swagger
                        .requestMatchers("/", "/index", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll() // Recursos estáticos

                        //Endpoints protegidos
                        //Zapatillas: CREATE,UPDATE,DELETE requieren autenticacion
                        .requestMatchers(HttpMethod.POST,"/api/v1/zapatillas/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,"/api/v1/zapatillas/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/zapatillas/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/zapatillas/**").authenticated()
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
