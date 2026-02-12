package es.pabloab.zapatillas.config;

import es.pabloab.zapatillas.rest.auth.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
 * Configuración de seguridad con MÚLTIPLES cadenas de filtros.
 *
 * ¿POR QUÉ MÚLTIPLES CADENAS?
 * ============================
 * La API REST y las páginas web tienen necesidades de seguridad diferentes:
 *
 * - API REST: Usa JWT (token en header), sin sesiones, sin CSRF
 * - Páginas web: Usa formulario de login, con sesiones (cookie JSESSIONID), con CSRF
 *
 * Spring Security evalúa las cadenas en orden (@Order). La primera que coincida
 * con la URL de la petición será la que se aplique.
 *
 * ORDEN DE EVALUACIÓN:
 * 1. @Order(1) - API: /api/**, /graphql, /ws/** → JWT, stateless
 * 2. @Order(2) - Swagger/OpenAPI → permitAll
 * 3. @Order(3) - H2 Console → permitAll (solo desarrollo)
 * 4. @Order(4) - Web: todo lo demás → form login, sesiones, CSRF
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    // =========================================================================
    // CADENA 1: API REST (JWT, stateless, sin CSRF)
    // =========================================================================
    // Esta cadena captura TODAS las peticiones a /api/**, /graphql, /ws/**
    // y las protege con JWT. No crea sesiones en el servidor.
    //
    // ¿Qué es @Order?
    // Es la prioridad. @Order(1) se evalúa primero. Si la URL coincide con
    // los requestMatchers de securityMatcher(), se aplica ESTA cadena y se
    // ignoran las demás.
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // securityMatcher: Define QUÉ URLs gestiona esta cadena
                // Solo las URLs que empiecen por /api/, /graphql, /ws/ o /error
                .securityMatcher("/api/**", "/graphql", "/graphiql/**", "/ws/**", "/error/**")

                // CSRF deshabilitado: Las APIs REST no usan formularios HTML,
                // así que no necesitan protección CSRF. Usan JWT en su lugar.
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // Auth endpoints: públicos (login y registro)
                        .requestMatchers("/api/v1/auth/**", "/api/*/auth/**").permitAll()
                        // GET zapatillas: público (cualquiera puede ver el catálogo)
                        .requestMatchers(HttpMethod.GET, "/api/v1/zapatillas/**").permitAll()
                        // GraphQL: público
                        .requestMatchers("/graphql", "/graphiql/**").permitAll()
                        // WebSockets: público
                        .requestMatchers("/ws/**").permitAll()
                        // Errores: público
                        .requestMatchers("/error/**").permitAll()
                        // Todo lo demás dentro de /api/**: requiere autenticación
                        // (las reglas específicas por rol se definen con @PreAuthorize en los controllers)
                        .anyRequest().authenticated()
                )

                // STATELESS: No se crean sesiones. Cada petición debe llevar su JWT.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                // Añadimos el filtro JWT ANTES del filtro de autenticación estándar
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // =========================================================================
    // CADENA 2: Swagger / OpenAPI (acceso libre)
    // =========================================================================
    // Swagger UI y los docs de la API son siempre públicos.
    @Bean
    @Order(2)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    // =========================================================================
    // CADENA 3: H2 Console (solo desarrollo)
    // =========================================================================
    // La consola H2 usa iframes internos, por eso deshabilitamos frameOptions.
    // En producción esto NO debería existir.
    @Bean
    @Order(3)
    public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/h2-console/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }

    // =========================================================================
    // CADENA 4: WEB (form login, sesiones, CSRF habilitado)
    // =========================================================================
    // Esta es la cadena "por defecto" - captura TODO lo que no haya coincidido
    // con las cadenas anteriores. Es decir: las páginas HTML del navegador.
    //
    // ¿Qué es formLogin?
    // Spring Security intercepta las peticiones a URLs protegidas y redirige
    // al usuario a /auth/login. Cuando el usuario envía el formulario,
    // Spring procesa el POST en /auth/login-post automáticamente.
    //
    // ¿Qué es CSRF?
    // Cross-Site Request Forgery: Un ataque donde un sitio malicioso envía
    // peticiones en nombre del usuario. Spring genera un token único por sesión
    // que debe incluirse en cada formulario HTML. Sin el token, el POST se rechaza.
    @Bean
    @Order(4)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Zona pública: catálogo, recursos estáticos
                        .requestMatchers("/public/**", "/", "/index").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                        // Zona admin: requiere rol ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Todo lo demás: requiere estar autenticado
                        .anyRequest().authenticated()
                )
                // Form login: Spring gestiona el flujo de autenticación
                .formLogin(form -> form
                        .loginPage("/auth/login")                // Página del formulario (GET)
                        .loginProcessingUrl("/auth/login-post")  // URL donde se envía el POST
                        .defaultSuccessUrl("/public/", true)     // A dónde ir tras login exitoso
                        .permitAll()
                )
                // Logout: Spring gestiona la destrucción de sesión
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")               // URL para hacer logout
                        .logoutSuccessUrl("/auth/login?logout")  // Redirige con parámetro ?logout
                        .permitAll()
                );
                // CSRF está HABILITADO por defecto en esta cadena (no lo deshabilitamos)
                // Los formularios Pebble deben incluir el token CSRF como campo oculto

        return http.build();
    }

    // =========================================================================
    // BEANS COMPARTIDOS
    // =========================================================================

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