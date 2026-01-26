package es.pabloab.zapatillas.config;

import es.pabloab.zapatillas.rest.user.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Clase utilitaria para trabajar con la seguridad de Spring Security.
 * 
 * Esta clase proporciona métodos estáticos para acceder al usuario autenticado
 * desde cualquier parte de la aplicación sin necesidad de inyectar dependencias.
 * 
 * ¿Por qué es útil?
 * - Centraliza el acceso al SecurityContext
 * - Evita duplicar código en múltiples controladores
 * - Facilita el testing (podemos mockear SecurityContext)
 */
@Slf4j
public class SecurityUtils {

    /**
     * Obtiene el usuario autenticado actual del SecurityContext.
     * 
     * @return El objeto User autenticado, o null si no hay usuario autenticado
     * 
     * Explicación:
     * - SecurityContextHolder es el contenedor de Spring Security que almacena
     *   la información de autenticación del request actual
     * - Authentication contiene los detalles del usuario autenticado
     * - El principal es el objeto UserDetails (en nuestro caso, User)
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No hay usuario autenticado en el contexto de seguridad");
            return null;
        }

        Object principal = authentication.getPrincipal();
        
        // Verificamos que el principal sea una instancia de User
        if (principal instanceof User) {
            return (User) principal;
        }
        
        log.warn("El principal no es una instancia de User: {}", principal.getClass().getName());
        return null;
    }

    /**
     * Obtiene el username del usuario autenticado actual.
     * 
     * @return El username del usuario autenticado, o null si no hay usuario autenticado
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // Si el principal es un UserDetails, obtenemos el username
        if (authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        
        // Si es un String (caso menos común), lo devolvemos directamente
        if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        
        return null;
    }

    /**
     * Verifica si el usuario actual tiene el rol especificado.
     * 
     * @param role El rol a verificar (ADMIN o USER)
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Verifica si el usuario actual es un administrador.
     * 
     * @return true si el usuario es ADMIN, false en caso contrario
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Verifica si el usuario actual es un usuario normal.
     * 
     * @return true si el usuario es USER, false en caso contrario
     */
    public static boolean isUser() {
        return hasRole("USER");
    }
}
