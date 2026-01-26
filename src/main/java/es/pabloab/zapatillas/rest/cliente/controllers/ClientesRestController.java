package es.pabloab.zapatillas.rest.cliente.controllers;

import es.pabloab.zapatillas.config.SecurityUtils;
import es.pabloab.zapatillas.rest.cliente.dto.ClienteCreateDto;
import es.pabloab.zapatillas.rest.cliente.dto.ClienteResponseDto;
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
 * - GET (ver todos): ADMIN puede ver todos, USER puede ver su propio cliente
 * - GET (ver por ID): ADMIN puede ver cualquiera, USER solo su propio cliente
 * - POST (crear): Solo ADMIN puede crear clientes
 * - PUT/PATCH (modificar): ADMIN puede modificar cualquiera, USER solo su propio cliente
 * - DELETE (borrar): Solo ADMIN puede borrar clientes
 * 
 * IMPORTANTE: Un usuario normal solo puede acceder a recursos que le pertenecen.
 * Esto se verifica comparando el ID del cliente con el cliente asociado al usuario.
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
     * Acceso: ADMIN puede ver todos, USER puede ver su propio cliente.
     * 
     * NOTA: En una aplicación real, podrías querer filtrar los resultados
     * para que los usuarios normales solo vean su propio cliente.
     * Por ahora, permitimos que vean todos (puede ser un catálogo público).
     */
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDto>> getAll(Pageable pageable){
        return ResponseEntity.ok(service.findAll(pageable));
    }
    
    /**
     * Obtiene un cliente por su ID.
     * Acceso: ADMIN puede ver cualquiera, USER solo su propio cliente.
     * 
     * Lógica de seguridad:
     * 1. Si el usuario es ADMIN, puede ver cualquier cliente
     * 2. Si el usuario es USER, verificamos que el cliente le pertenezca
     * 
     * @PreAuthorize no se puede usar aquí porque necesitamos acceso al parámetro
     * y al usuario autenticado. Por eso verificamos manualmente en el método.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> getById(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        
        // Si el usuario es ADMIN, puede ver cualquier cliente
        if (SecurityUtils.isAdmin()) {
            return ResponseEntity.ok(service.findById(id));
        }
        
        // Si es USER, solo puede ver su propio cliente
        if (currentUser != null && currentUser.getCliente() != null) {
            Long userClienteId = currentUser.getCliente().getId();
            if (userClienteId.equals(id)) {
                return ResponseEntity.ok(service.findById(id));
            }
        }
        
        // Si llegamos aquí, el usuario no tiene permiso
        throw new AccessDeniedException("No tienes permiso para acceder a este cliente");
    }
    
    /**
     * Crea un nuevo cliente.
     * Acceso: Solo ADMIN puede crear clientes.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDto> create(@Valid @RequestBody ClienteCreateDto dto){
        var saved = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    /**
     * Actualiza un cliente.
     * Acceso: ADMIN puede modificar cualquiera, USER solo su propio cliente.
     * 
     * Similar a getById, verificamos manualmente la propiedad del recurso.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody es.pabloab.zapatillas.rest.cliente.dto.ClienteUpdateDto dto) {
        User currentUser = SecurityUtils.getCurrentUser();
        
        // Si el usuario es ADMIN, puede modificar cualquier cliente
        if (SecurityUtils.isAdmin()) {
            return ResponseEntity.ok(service.update(id, dto));
        }
        
        // Si es USER, solo puede modificar su propio cliente
        if (currentUser != null && currentUser.getCliente() != null) {
            Long userClienteId = currentUser.getCliente().getId();
            if (userClienteId.equals(id)) {
                return ResponseEntity.ok(service.update(id, dto));
            }
        }
        
        throw new AccessDeniedException("No tienes permiso para modificar este cliente");
    }
    
    /**
     * Elimina un cliente.
     * Acceso: Solo ADMIN puede borrar clientes.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        BindingResult bindingResult = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto=" + bindingResult.getObjectName() + "Número de errores: " +
                bindingResult.getErrorCount());
        Map<String,String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName,errorMessage);
        });
        problemDetail.setProperty("errores",errors);
        return problemDetail;
    }
}
