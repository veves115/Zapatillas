package es.pabloab.zapatillas.rest.cliente.controllers;

import es.pabloab.zapatillas.config.SecurityUtils;
import es.pabloab.zapatillas.rest.cliente.dto.ClienteCreateDto;
import es.pabloab.zapatillas.rest.cliente.dto.ClienteResponseDto;
import es.pabloab.zapatillas.rest.cliente.dto.ClienteUpdateDto;
import es.pabloab.zapatillas.rest.cliente.services.ClienteService;
import es.pabloab.zapatillas.rest.user.models.User;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para gestionar clientes.
 *
 * REGLAS DE SEGURIDAD:
 *
 * ADMIN (puede hacer todo):
 * - Ver todos los clientes
 * - Ver cualquier cliente por ID
 * - Crear clientes
 * - Modificar cualquier cliente
 * - Eliminar cualquier cliente
 *
 * USER (acceso limitado):
 * - Ver todos los clientes (catálogo público)
 * - Ver solo SU PROPIO cliente por ID
 * - NO puede crear clientes (solo ADMIN)
 * - Modificar solo SU PROPIO cliente
 * - NO puede eliminar clientes (solo ADMIN)
 *
 * IMPORTANTE:
 * - Un usuario tiene una relación OneToOne con Cliente
 * - El cliente asociado se obtiene mediante user.getCliente()
 * - Si un USER intenta acceder a un cliente que no es el suyo, se lanza AccessDeniedException (403)
 */
@RestController
@RequestMapping("api/v1/usuarios")
@Slf4j
public class ClientesRestController {
    private final ClienteService service;

    public ClientesRestController(ClienteService service) {
        this.service = service;
    }

    /**
     * Obtiene todos los clientes (paginados).
     *
     * Acceso: Todos los usuarios autenticados pueden ver el listado completo.
     * (Se considera un catálogo público para usuarios autenticados)
     */
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDto>> getAll(Pageable pageable){
        log.info("GET /api/v1/usuarios - Obteniendo todos los clientes");
        return ResponseEntity.ok(service.findAll(pageable));
    }

    /**
     * Obtiene un cliente por su ID.
     *
     * REGLAS DE ACCESO:
     * - ADMIN: Puede ver cualquier cliente
     * - USER: Solo puede ver su propio cliente asociado
     *
     * @param id El ID del cliente a obtener
     * @return El cliente si tiene permisos
     * @throws AccessDeniedException si un USER intenta ver un cliente que no es el suyo
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> getById(@PathVariable Long id) {
        log.info("GET /api/v1/usuarios/{} - Usuario intentando acceder", id);

        User currentUser = SecurityUtils.getCurrentUser();

        // ADMIN puede ver cualquier cliente
        if (SecurityUtils.isAdmin()) {
            log.debug("Usuario ADMIN accediendo a cliente id={}", id);
            return ResponseEntity.ok(service.findById(id));
        }

        // USER solo puede ver su propio cliente
        if (currentUser != null && currentUser.getCliente() != null) {
            Long userClienteId = currentUser.getCliente().getId();

            if (userClienteId.equals(id)) {
                log.debug("Usuario USER accediendo a su propio cliente id={}", id);
                return ResponseEntity.ok(service.findById(id));
            } else {
                log.warn("Usuario {} intentó acceder a cliente {} que no le pertenece (su cliente es {})",
                        currentUser.getUsername(), id, userClienteId);
                throw new AccessDeniedException("No tienes permiso para acceder a este cliente");
            }
        }

        // Usuario sin cliente asociado
        log.warn("Usuario {} no tiene cliente asociado e intentó acceder a cliente id={}",
                currentUser != null ? currentUser.getUsername() : "unknown", id);
        throw new AccessDeniedException("No tienes un cliente asociado");
    }

    /**
     * Crea un nuevo cliente.
     *
     * Acceso: Solo ADMIN puede crear clientes.
     *
     * @PreAuthorize verifica que el usuario tenga rol ADMIN antes de ejecutar el método.
     * Si no tiene el rol, Spring Security lanza AccessDeniedException (403 Forbidden).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDto> create(@Valid @RequestBody ClienteCreateDto dto){
        log.info("POST /api/v1/usuarios - ADMIN creando cliente: {}", dto.getEmail());
        var saved = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Actualiza un cliente.
     *
     * REGLAS DE ACCESO:
     * - ADMIN: Puede modificar cualquier cliente
     * - USER: Solo puede modificar su propio cliente asociado
     *
     * @param id El ID del cliente a actualizar
     * @param dto Los datos a actualizar
     * @return El cliente actualizado
     * @throws AccessDeniedException si un USER intenta modificar un cliente que no es el suyo
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ClienteUpdateDto dto) {
        log.info("PUT /api/v1/usuarios/{} - Actualizando cliente", id);

        User currentUser = SecurityUtils.getCurrentUser();

        // ADMIN puede modificar cualquier cliente
        if (SecurityUtils.isAdmin()) {
            log.debug("ADMIN actualizando cliente id={}", id);
            return ResponseEntity.ok(service.update(id, dto));
        }

        // USER solo puede modificar su propio cliente
        if (currentUser != null && currentUser.getCliente() != null) {
            Long userClienteId = currentUser.getCliente().getId();

            if (userClienteId.equals(id)) {
                log.debug("USER actualizando su propio cliente id={}", id);
                return ResponseEntity.ok(service.update(id, dto));
            } else {
                log.warn("Usuario {} intentó modificar cliente {} que no le pertenece",
                        currentUser.getUsername(), id);
                throw new AccessDeniedException("No tienes permiso para modificar este cliente");
            }
        }

        log.warn("Usuario sin cliente asociado intentó modificar cliente id={}", id);
        throw new AccessDeniedException("No tienes un cliente asociado");
    }

    /**
     * Elimina un cliente.
     *
     * Acceso: Solo ADMIN puede eliminar clientes.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        log.info("DELETE /api/v1/usuarios/{} - ADMIN eliminando cliente", id);
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Maneja excepciones de validación.
     *
     * Este método se ejecuta automáticamente cuando hay errores de validación
     * en los DTOs (anotaciones @Valid, @NotBlank, @Email, etc.)
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        BindingResult bindingResult = ex.getBindingResult();

        problemDetail.setDetail("Falló la validación para el objeto='" + bindingResult.getObjectName() +
                "'. Número de errores: " + bindingResult.getErrorCount());

        Map<String,String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        problemDetail.setProperty("errores", errors);

        return problemDetail;
    }
}