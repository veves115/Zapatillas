package es.pabloab.zapatillas.rest.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para devolver información de un usuario.
 * 
 * IMPORTANTE: No incluimos la contraseña por seguridad.
 * Solo incluimos información que es segura de exponer públicamente.
 */
@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String nombre;
    private String apellidos;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    // No incluimos la contraseña por seguridad
    // No incluimos roles directamente, pero podrías si lo necesitas
}
