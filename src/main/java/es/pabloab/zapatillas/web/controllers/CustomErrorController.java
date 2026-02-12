package es.pabloab.zapatillas.web.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador personalizado de errores.
 *
 * ¿QUÉ ES ErrorController?
 * =========================
 * Es una interfaz de Spring Boot que permite interceptar TODOS los errores HTTP.
 * Cuando ocurre un error (404, 403, 500...), Spring Boot redirige internamente
 * a /error. Este controller captura esa petición y muestra una página bonita.
 *
 * SIN este controller, Spring muestra la "Whitelabel Error Page" (fea y poco útil).
 *
 * ¿CÓMO SABE QUÉ ERROR OCURRIÓ?
 * Spring Boot pone el código de estado en un atributo del request llamado
 * RequestDispatcher.ERROR_STATUS_CODE. Lo leemos y mostramos un mensaje apropiado.
 *
 * ERRORES COMUNES:
 * - 404: La URL no existe (el usuario escribió mal la dirección)
 * - 403: No tiene permiso (es USER pero intenta acceder a /admin/)
 * - 500: Error interno del servidor (bug en el código)
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Leer el código de estado HTTP del error
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;

        // Determinar título y mensaje según el código
        String errorTitle;
        String errorMessage;
        String errorIcon;

        switch (statusCode) {
            case 404:
                errorTitle = "Página no encontrada";
                errorMessage = "La página que buscas no existe o ha sido movida.";
                errorIcon = "bi-search";
                break;
            case 403:
                errorTitle = "Acceso denegado";
                errorMessage = "No tienes permisos para acceder a esta página.";
                errorIcon = "bi-shield-exclamation";
                break;
            case 401:
                errorTitle = "No autenticado";
                errorMessage = "Necesitas iniciar sesión para acceder a esta página.";
                errorIcon = "bi-lock";
                break;
            default:
                errorTitle = "Error del servidor";
                errorMessage = "Ha ocurrido un error inesperado. Inténtalo de nuevo más tarde.";
                errorIcon = "bi-exclamation-triangle";
                break;
        }

        model.addAttribute("errorCode", statusCode);
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("errorIcon", errorIcon);

        return "error";
    }
}
