package es.pabloab.zapatillas.web.controllers;

import es.pabloab.zapatillas.rest.user.models.Role;
import es.pabloab.zapatillas.rest.user.models.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * GlobalControllerAdvice: Inyecta datos globales en TODAS las plantillas Pebble.
 *
 * ¿QUÉ ES ESTO?
 * ==============
 * @ControllerAdvice es un componente que se aplica a todos los @Controller.
 * Cada método @ModelAttribute se ejecuta ANTES de renderizar cualquier plantilla.
 * El valor devuelto se pone en el "model" y queda disponible en Pebble con {{ nombre }}.
 *
 * ¿POR QUÉ LO NECESITAMOS?
 * =========================
 * El navbar necesita saber si el usuario está logueado para mostrar "Logout" o "Login".
 * El footer necesita el año actual. Los formularios necesitan el token CSRF.
 * Sin este @ControllerAdvice, tendríamos que añadir estos datos en CADA controlador.
 *
 * DATOS INYECTADOS:
 * - appName, appDescription → Metadatos de la app
 * - currentUser → Objeto User completo (o null)
 * - isAuthenticated → boolean: ¿está logueado?
 * - isAdmin → boolean: ¿tiene rol ADMIN?
 * - username → String: nombre de usuario
 * - userRoles → String: roles separados por coma ("ADMIN, USER")
 * - csrfToken, csrfParamName, csrfHeaderName → Para formularios HTML
 * - currentYear, currentMonth, currentDateTime → Datos temporales
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${spring.application.name}")
    private String appName;

    // ------------- METADATOS DE LA APP -------------

    @ModelAttribute("appName")
    public String getAppName() {
        return appName;
    }

    @ModelAttribute("appDescription")
    public String getAppDescription() {
        return "API REST de Zapatillas con Spring Boot";
    }

    // ------------- AUTENTICACIÓN Y AUTORIZACIÓN -------------
    // Estos datos permiten que las plantillas muestren/oculten elementos
    // según si el usuario está logueado y su rol.

    /**
     * Devuelve el objeto User completo del usuario autenticado, o null si es anónimo.
     *
     * ¿Cómo funciona?
     * SecurityContextHolder guarda la info de autenticación del hilo actual.
     * Si la autenticación es "anónima" (no logueado), devolvemos null.
     * Si está logueado, el principal es nuestro objeto User (implementa UserDetails).
     */
    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            if (principal instanceof User user) {
                return user;
            }
        }
        return null;
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    /**
     * Comprueba si el usuario actual tiene rol ADMIN.
     * Recorre los roles del User y busca "ADMIN" en la lista.
     */
    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        User user = getCurrentUser();
        if (user != null && user.getRoles() != null) {
            return user.getRoles().contains(Role.ADMIN);
        }
        return false;
    }

    @ModelAttribute("username")
    public String getUsername() {
        User user = getCurrentUser();
        if (user != null) {
            return user.getNombre() + " " + user.getApellidos();
        }
        return "";
    }

    @ModelAttribute("userRoles")
    public String getUserRoles() {
        User user = getCurrentUser();
        if (user != null && user.getRoles() != null) {
            return user.getRoles().stream()
                    .map(Role::name)
                    .collect(Collectors.joining(", "));
        }
        return "";
    }

    // ------------- CSRF (protección contra ataques) -------------
    // Los formularios HTML de la cadena web DEBEN incluir el token CSRF.
    // Estos métodos hacen que el token esté disponible en las plantillas.
    //
    // En Pebble se usa así:
    //   <input type="hidden" name="{{ csrfParamName }}" value="{{ csrfToken }}">

    @ModelAttribute("csrfToken")
    public String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrf != null ? csrf.getToken() : "";
    }

    @ModelAttribute("csrfParamName")
    public String getCsrfParamName(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrf != null ? csrf.getParameterName() : "_csrf";
    }

    @ModelAttribute("csrfHeaderName")
    public String getCsrfHeaderName(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrf != null ? csrf.getHeaderName() : "X-CSRF-TOKEN";
    }

    // ------------- DATOS TEMPORALES -------------

    @ModelAttribute("currentYear")
    public String getCurrentYear() {
        return String.valueOf(LocalDate.now().getYear());
    }

    @ModelAttribute("currentMonth")
    public String getCurrentMonth() {
        return LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
    }

    @ModelAttribute("currentDateTime")
    public String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }
}
