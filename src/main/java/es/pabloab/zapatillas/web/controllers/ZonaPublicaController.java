package es.pabloab.zapatillas.web.controllers;

import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.rest.zapatillas.services.ZapatillasService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de la zona pública.
 *
 * Gestiona las páginas que CUALQUIERA puede ver sin autenticarse:
 * - /public/ → Catálogo de zapatillas (la página principal)
 * - / → Redirige a /public/
 *
 * ¿Por qué /public/?
 * En SecurityConfig (cadena 4), definimos que /public/** es permitAll().
 * Así separamos claramente las zonas: /public (todos), /app (autenticados), /admin (admin).
 */
@RequiredArgsConstructor
@Controller
public class ZonaPublicaController {
    private final ZapatillasService zapatillasService;

    // Redirige la raíz al catálogo público
    @GetMapping({"/", "/index"})
    public String root() {
        return "redirect:/public/";
    }

    // Catálogo público de zapatillas con paginación
    @GetMapping({"/public", "/public/", "/public/index"})
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<ZapatillaResponseDto> zapatillasPage = zapatillasService.findAll(
                null, null, pageable);

        model.addAttribute("page", zapatillasPage);
        return "index";
    }
}
