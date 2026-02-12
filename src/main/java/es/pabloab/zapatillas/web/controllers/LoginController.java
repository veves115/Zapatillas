package es.pabloab.zapatillas.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para la página de login.
 *
 * FLUJO DE AUTENTICACIÓN WEB:
 * ============================
 * 1. GET /auth/login → Este controller → Muestra el formulario de login
 * 2. POST /auth/login-post → Spring Security lo gestiona automáticamente
 *    - Éxito → redirige a /public/
 *    - Fallo → redirige a /auth/login?error
 * 3. POST /auth/logout → Spring Security destruye la sesión → redirige a /auth/login?logout
 *
 * ¿POR QUÉ SOLO TIENE UN @GetMapping?
 * Porque el POST lo maneja Spring Security internamente.
 * Nosotros solo mostramos la página del formulario.
 * En SecurityConfig definimos:
 *   .loginPage("/auth/login")              → esta página
 *   .loginProcessingUrl("/auth/login-post") → Spring procesa el POST aquí
 */
@Controller
public class LoginController {

    @GetMapping("/auth/login")
    public String loginPage() {
        // Devuelve el nombre de la plantilla Pebble (sin extensión)
        // Pebble buscará: templates/login.peb.html
        return "login";
    }
}
