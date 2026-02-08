package es.pabloab.zapatillas.rest.auth.controllers;

import es.pabloab.zapatillas.rest.auth.dto.AuthResponseDto;
import es.pabloab.zapatillas.rest.auth.dto.LogingDto;
import es.pabloab.zapatillas.rest.auth.dto.RegisterDto;
import es.pabloab.zapatillas.rest.auth.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación de usuarios.
 *
 * Endpoints:
 * - POST /api/v1/auth/register - Registrar nuevo usuario
 * - POST /api/v1/auth/login - Iniciar sesión
 *
 * IMPORTANTE:
 * - Los endpoints son públicos (no requieren autenticación)
 * - Se usa @Valid para validar los DTOs automáticamente
 * - Se capturan excepciones de validación y se devuelven errores estructurados
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Login y registro de usuarios")
public class UserAuthController {

    private final AuthenticationService authenticationService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param registerDto Datos del nuevo usuario (validados con @Valid)
     * @return Respuesta con token JWT y datos del usuario
     */
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario y devuelve token JWT")
    @ApiResponse(responseCode = "201", description = "Usuario registrado")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto) {
        log.info("Solicitud de registro para username: {}", registerDto.getUsername());

        AuthResponseDto response = authenticationService.register(registerDto);

        log.info("Usuario registrado exitosamente: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Inicia sesión de un usuario existente.
     *
     * @param loginDto Credenciales del usuario (validadas con @Valid)
     * @return Respuesta con token JWT y datos del usuario
     */
    @Operation(summary = "Iniciar sesión", description = "Autentica usuario y devuelve token JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LogingDto loginDto) {
        log.info("Solicitud de login para username: {}", loginDto.getUsername());

        AuthResponseDto response = authenticationService.login(loginDto);

        log.info("Usuario autenticado exitosamente: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }

}