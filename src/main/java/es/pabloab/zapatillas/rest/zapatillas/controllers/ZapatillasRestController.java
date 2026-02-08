package es.pabloab.zapatillas.rest.zapatillas.controllers;


import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.rest.zapatillas.exceptions.ZapatillaNotFoundException;
import es.pabloab.zapatillas.rest.zapatillas.services.ZapatillasService;
import es.pabloab.zapatillas.utils.pagination.PageResponse;
import es.pabloab.zapatillas.utils.pagination.PaginationLinksUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Controlador REST para gestionar zapatillas.
 * 
 * REGLAS DE SEGURIDAD:
 * - GET (ver): Todos los usuarios autenticados pueden ver zapatillas
 * - POST (crear): Solo ADMIN puede crear zapatillas
 * - PUT/PATCH (modificar): Solo ADMIN puede modificar zapatillas
 * - DELETE (borrar): Solo ADMIN puede borrar zapatillas
 * 
 * @PreAuthorize: Esta anotación evalúa la expresión SpEL antes de ejecutar el método.
 * Si la expresión retorna false, se lanza AccessDeniedException (403 Forbidden).
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/zapatillas")
@Tag(name = "Zapatillas", description = "Gestión de zapatillas")
public class ZapatillasRestController {

    private final ZapatillasService service;
    private final PaginationLinksUtils paginationLinksUtils;

    /**
     * Obtiene todas las zapatillas (paginadas).
     * Acceso: Todos los usuarios autenticados pueden ver zapatillas.
     */
    @Operation(summary = "Listar zapatillas", description = "Obtiene todas las zapatillas con paginación y filtros opcionales")
    @GetMapping()
    public ResponseEntity<PageResponse<ZapatillaResponseDto>> getAll(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request){
        log.info("Buscando zapatillas por marca={} tipo={}", marca, tipo);

        //1.Construimos el Sort a partir de los parámetros
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //2. Construimos el Pageable con page + size + sort
        Pageable pageable = PageRequest.of(page, size, sort);

        //3. LLamamos al servicio
        Page<ZapatillaResponseDto> pageResult = service.findAll(marca, tipo, pageable);

        //4. Generamos la cabecera Link usando la URL de la petición actual
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        String linkHeader = paginationLinksUtils.createLinkHeader(pageResult, uriBuilder);

        //5. Devolvemos PageResponse + cabecera Link
        return ResponseEntity.ok()
                .header("link", linkHeader)
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Obtiene una zapatilla por su ID.
     * Acceso: Todos los usuarios autenticados pueden ver zapatillas.
     */
    @Operation(summary = "Obtener zapatilla por ID")
    @ApiResponse(responseCode = "200", description = "Zapatilla encontrada")
    @ApiResponse(responseCode = "404", description = "Zapatilla no encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<ZapatillaResponseDto> getById(@PathVariable Long id) throws ZapatillaNotFoundException {
        log.info("Buscando zapatilla por id={}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Crea una nueva zapatilla.
     * Acceso: Solo ADMIN puede crear zapatillas.
     * 
     * @PreAuthorize("hasRole('ADMIN')"): Verifica que el usuario tenga el rol ADMIN.
     * hasRole() es una función de Spring Security que verifica si el usuario tiene
     * la autoridad "ROLE_ADMIN" (Spring Security automáticamente añade el prefijo "ROLE_").
     */
    @Operation(summary = "Crear zapatilla", description = "Solo ADMIN")
    @ApiResponse(responseCode = "201", description = "Zapatilla creada")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZapatillaResponseDto> create(@Valid @RequestBody ZapatillaCreateDto dto) {
        log.info("Creando zapatilla={}", dto);
        var saved = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Actualiza completamente una zapatilla.
     * Acceso: Solo ADMIN puede modificar zapatillas.
     */
    @Operation(summary = "Actualizar zapatilla completa", description = "Solo ADMIN")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZapatillaResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ZapatillaUpdateDto dto) throws ZapatillaNotFoundException {
        log.info("Actualizando zapatilla id={} con datos={}", id, dto);
        return ResponseEntity.ok(service.update(id, dto));
    }

    /**
     * Actualiza parcialmente una zapatilla.
     * Acceso: Solo ADMIN puede modificar zapatillas.
     */
    @Operation(summary = "Actualizar zapatilla parcialmente", description = "Solo ADMIN")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZapatillaResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody
    ZapatillaUpdateDto dto) throws ZapatillaNotFoundException {
        log.info("Actualizando parcialmente zapatilla id={}", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    /**
     * Elimina una zapatilla.
     * Acceso: Solo ADMIN puede borrar zapatillas.
     */
    @Operation(summary = "Eliminar zapatilla", description = "Solo ADMIN")
    @ApiResponse(responseCode = "204", description = "Zapatilla eliminada")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZapatillaResponseDto>delete(@PathVariable Long id) {
        log.info("Eliminando zapatilla id={}", id);
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

