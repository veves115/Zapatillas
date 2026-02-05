package es.pabloab.zapatillas.rest.auth.controllers;

import es.pabloab.zapatillas.rest.auth.dto.AuthResponseDto;
import es.pabloab.zapatillas.rest.auth.dto.LogingDto;
import es.pabloab.zapatillas.rest.auth.dto.RegisterDto;
import es.pabloab.zapatillas.rest.auth.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
public class UserAuthController {

    private final AuthenticationService authenticationService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param registerDto Datos del nuevo usuario (validados con @Valid)
     * @return Respuesta con token JWT y datos del usuario
     */
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
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LogingDto loginDto) {
        log.info("Solicitud de login para username: {}", loginDto.getUsername());

        AuthResponseDto response = authenticationService.login(loginDto);

        log.info("Usuario autenticado exitosamente: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Manejador de excepciones de validación.
     *
     * Se ejecuta automáticamente cuando hay errores en las validaciones
     * de los DTOs (@NotBlank, @Email, @Length, etc.)
     *
     * @param ex Excepción de validación
     * @return Detalle del problema con los errores de validación
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Errores de validación en la petición");

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail(
                "Falló la validación para el objeto='" + result.getObjectName() +
                        "'. Núm. errores: " + result.getErrorCount()
        );

        // Crear mapa con los errores campo por campo
        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errores", errores);

        log.warn("Errores de validación: {}", errores);
        return problemDetail;
    }
}