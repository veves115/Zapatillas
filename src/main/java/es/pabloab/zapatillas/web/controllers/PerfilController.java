package es.pabloab.zapatillas.web.controllers;

import es.pabloab.zapatillas.rest.user.models.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador de la zona de usuario autenticado.
 *
 * ACCESO: Cualquier usuario logueado (USER o ADMIN).
 * Configurado en SecurityConfig cadena 4: .anyRequest().authenticated()
 * (las rutas /app/** no están en permitAll, así que requieren login)
 *
 * ¿QUÉ ES @AuthenticationPrincipal?
 * ==================================
 * Es una anotación de Spring Security que inyecta directamente el objeto
 * del usuario autenticado como parámetro del método. Es un atajo para:
 *   SecurityContextHolder.getContext().getAuthentication().getPrincipal()
 *
 * Como nuestro User implementa UserDetails, Spring sabe que el "principal"
 * es un objeto User y nos lo da ya casteado.
 */
@Controller
@RequestMapping("/app")
public class
PerfilController {
    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "app/perfil";
    }
}
