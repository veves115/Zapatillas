package es.pabloab.zapatillas.rest.user.controllers;

import es.pabloab.zapatillas.config.SecurityUtils;
import es.pabloab.zapatillas.rest.user.dto.UserResponseDto;
import es.pabloab.zapatillas.rest.user.dto.UserUpdateDto;
import es.pabloab.zapatillas.rest.user.models.User;
import es.pabloab.zapatillas.rest.user.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar perfiles de usuario.
 * 
 * REGLAS DE SEGURIDAD:
 * - GET (ver perfil): Un usuario puede ver su propio perfil, ADMIN puede ver cualquier perfil
 * - PUT/PATCH (modificar perfil): Un usuario puede modificar su propio perfil, ADMIN puede modificar cualquier perfil
 * 
 * IMPORTANTE: Los usuarios normales solo pueden acceder a su propio perfil.
 * Esto es diferente a los clientes, donde un usuario puede tener un cliente asociado.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "Usuarios", description = "Gestión de perfiles de usuario")
public class UsersRestController {
    
    private final UserService userService;

    /**
     * Obtiene el perfil del usuario autenticado actual.
     * 
     * Este endpoint es útil para que un usuario obtenga su propia información
     * sin necesidad de conocer su ID.
     * 
     * Acceso: Todos los usuarios autenticados pueden ver su propio perfil.
     */
    @Operation(summary = "Obtener mi perfil")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyProfile() {
        User currentUser = SecurityUtils.getCurrentUser();
        
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        
        return ResponseEntity.ok(userService.findById(currentUser.getId()));
    }

    /**
     * Obtiene un usuario por su ID.
     * 
     * Acceso: Un usuario puede ver su propio perfil, ADMIN puede ver cualquier perfil.
     * 
     * Lógica de seguridad:
     * 1. Si el usuario es ADMIN, puede ver cualquier perfil
     * 2. Si el usuario es USER, solo puede ver su propio perfil
     */
    @Operation(summary = "Obtener usuario por ID", description = "ADMIN ve cualquiera, USER solo el suyo")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        
        // Si el usuario es ADMIN, puede ver cualquier perfil
        if (SecurityUtils.isAdmin()) {
            return ResponseEntity.ok(userService.findById(id));
        }
        
        // Si es USER, solo puede ver su propio perfil
        if (currentUser != null && currentUser.getId().equals(id)) {
            return ResponseEntity.ok(userService.findById(id));
        }
        
        throw new AccessDeniedException("No tienes permiso para acceder a este perfil");
    }

    /**
     * Actualiza el perfil del usuario autenticado actual.
     * 
     * Este endpoint permite a un usuario actualizar su propio perfil
     * sin necesidad de conocer su ID.
     * 
     * Acceso: Todos los usuarios autenticados pueden modificar su propio perfil.
     */
    @Operation(summary = "Actualizar mi perfil")
    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateMyProfile(@Valid @RequestBody UserUpdateDto dto) {
        User currentUser = SecurityUtils.getCurrentUser();
        
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        
        return ResponseEntity.ok(userService.update(currentUser.getId(), dto));
    }

    /**
     * Actualiza un usuario por su ID.
     * 
     * Acceso: Un usuario puede modificar su propio perfil, ADMIN puede modificar cualquier perfil.
     */
    @Operation(summary = "Actualizar usuario por ID", description = "ADMIN cualquiera, USER solo el suyo")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto dto) {
        User currentUser = SecurityUtils.getCurrentUser();
        
        // Si el usuario es ADMIN, puede modificar cualquier perfil
        if (SecurityUtils.isAdmin()) {
            return ResponseEntity.ok(userService.update(id, dto));
        }
        
        // Si es USER, solo puede modificar su propio perfil
        if (currentUser != null && currentUser.getId().equals(id)) {
            return ResponseEntity.ok(userService.update(id, dto));
        }
        
        throw new AccessDeniedException("No tienes permiso para modificar este perfil");
    }

}
