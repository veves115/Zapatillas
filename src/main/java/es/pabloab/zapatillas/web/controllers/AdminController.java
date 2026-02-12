package es.pabloab.zapatillas.web.controllers;

import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.rest.zapatillas.services.ZapatillasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web del panel de administración.
 *
 * ¿QUÉ ES ESTE CONTROLLER?
 * =========================
 * A diferencia del ZapatillasRestController (que devuelve JSON para apps/Postman),
 * este controller devuelve PLANTILLAS HTML renderizadas por Pebble.
 *
 * ACCESO: Solo usuarios con rol ADMIN (configurado en SecurityConfig cadena 4:
 *         .requestMatchers("/admin/**").hasRole("ADMIN"))
 *
 * PATRÓN PRG (Post-Redirect-Get):
 * ================================
 * Todos los POST hacen redirect después de procesar. ¿Por qué?
 * Si el usuario pulsa F5 después de un POST sin redirect, el navegador
 * re-enviaría el formulario (creando/editando duplicados). Con redirect:
 *   POST /admin/zapatillas/new → procesa → redirect:/admin/zapatillas → GET lista
 * Así F5 solo recarga la lista, no re-envía el formulario.
 *
 * RUTAS:
 * ======
 * GET  /admin/zapatillas              → Lista paginada
 * GET  /admin/zapatillas/{id}         → Detalle de una zapatilla
 * GET  /admin/zapatillas/new          → Formulario de creación (vacío)
 * POST /admin/zapatillas/new          → Procesar creación
 * GET  /admin/zapatillas/{id}/edit    → Formulario de edición (con datos)
 * POST /admin/zapatillas/{id}/edit    → Procesar edición
 * POST /admin/zapatillas/{id}/delete  → Procesar borrado
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/zapatillas")
public class AdminController {

    private final ZapatillasService zapatillasService;

    // =========================================================================
    // LISTAR (GET /admin/zapatillas)
    // =========================================================================
    // Muestra una tabla con todas las zapatillas, paginada.
    // Los parámetros de query (?page=0&size=10&marca=Nike) son opcionales.
    @GetMapping
    public String lista(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size,
                        @RequestParam(name = "marca", required = false) String marca,
                        @RequestParam(name = "tipo", required = false) String tipo) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<ZapatillaResponseDto> zapatillasPage = zapatillasService.findAll(marca, tipo, pageable);

        model.addAttribute("page", zapatillasPage);
        model.addAttribute("marca", marca);
        model.addAttribute("tipo", tipo);

        return "admin/zapatillas/lista";
    }

    // =========================================================================
    // DETALLE (GET /admin/zapatillas/{id})
    // =========================================================================
    // Muestra todos los datos de una zapatilla específica.
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        ZapatillaResponseDto zapatilla = zapatillasService.findById(id);
        model.addAttribute("zapatilla", zapatilla);
        return "admin/zapatillas/detalle";
    }

    // =========================================================================
    // CREAR - MOSTRAR FORMULARIO (GET /admin/zapatillas/new)
    // =========================================================================
    // Muestra un formulario vacío para crear una nueva zapatilla.
    //
    // ¿Por qué añadimos un DTO vacío al model?
    // Porque el formulario Pebble necesita un objeto "zapatilla" para rellenar
    // los campos. Al crear, el objeto está vacío. Al editar, tiene datos.
    // Así reutilizamos el mismo formulario para ambos casos.
    @GetMapping("/new")
    public String crearForm(Model model) {
        model.addAttribute("zapatilla", ZapatillaCreateDto.builder().build());
        model.addAttribute("isNew", true);
        return "admin/zapatillas/form";
    }

    // =========================================================================
    // CREAR - PROCESAR FORMULARIO (POST /admin/zapatillas/new)
    // =========================================================================
    // Recibe los datos del formulario, los valida, y si son correctos,
    // crea la zapatilla y redirige a la lista con un mensaje de éxito.
    //
    // ¿Qué es BindingResult?
    // Es un objeto que Spring rellena con los errores de validación.
    // Si el DTO tiene @NotBlank y el campo está vacío, BindingResult
    // contendrá ese error. Lo comprobamos con hasErrors().
    //
    // ¿Qué es RedirectAttributes?
    // Permite enviar datos "flash" que sobreviven a una redirección.
    // addFlashAttribute("successMessage", "...") → el fragment messages.peb.html lo muestra.
    @PostMapping("/new")
    public String crearProcess(@Valid @ModelAttribute("zapatilla") ZapatillaCreateDto dto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        // Si hay errores de validación, volvemos al formulario
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", true);
            return "admin/zapatillas/form";
        }

        try {
            zapatillasService.save(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Zapatilla creada correctamente");
            return "redirect:/admin/zapatillas";
        } catch (Exception e) {
            log.error("Error al crear zapatilla: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al crear la zapatilla: " + e.getMessage());
            model.addAttribute("isNew", true);
            return "admin/zapatillas/form";
        }
    }

    // =========================================================================
    // EDITAR - MOSTRAR FORMULARIO (GET /admin/zapatillas/{id}/edit)
    // =========================================================================
    // Carga la zapatilla existente y la pone en el formulario para editarla.
    // Reutiliza el mismo form.peb.html que la creación, pero con isNew=false.
    @GetMapping("/{id}/edit")
    public String editarForm(@PathVariable Long id, Model model) {
        ZapatillaResponseDto zapatilla = zapatillasService.findById(id);
        model.addAttribute("zapatilla", zapatilla);
        model.addAttribute("isNew", false);
        return "admin/zapatillas/form";
    }

    // =========================================================================
    // EDITAR - PROCESAR FORMULARIO (POST /admin/zapatillas/{id}/edit)
    // =========================================================================
    @PostMapping("/{id}/edit")
    public String editarProcess(@PathVariable Long id,
                                @Valid @ModelAttribute("zapatilla") ZapatillaUpdateDto dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", false);
            model.addAttribute("zapatillaId", id);
            return "admin/zapatillas/form";
        }

        try {
            zapatillasService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Zapatilla actualizada correctamente");
            return "redirect:/admin/zapatillas";
        } catch (Exception e) {
            log.error("Error al actualizar zapatilla: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al actualizar: " + e.getMessage());
            model.addAttribute("isNew", false);
            model.addAttribute("zapatillaId", id);
            return "admin/zapatillas/form";
        }
    }

    // =========================================================================
    // BORRAR (POST /admin/zapatillas/{id}/delete)
    // =========================================================================
    // ¿Por qué POST y no DELETE?
    // Los formularios HTML solo soportan GET y POST. No puedes hacer
    // method="DELETE" en un <form>. Por eso usamos POST para borrar desde web.
    // (La API REST sí usa DELETE porque los clientes HTTP lo soportan.)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            zapatillasService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Zapatilla eliminada correctamente");
        } catch (Exception e) {
            log.error("Error al eliminar zapatilla: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/zapatillas";
    }
}
