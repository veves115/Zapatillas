package es.pabloab.zapatillas.rest.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * DTO para actualizar información de un usuario.
 * 
 * IMPORTANTE: 
 * - No permitimos cambiar el username (es único y puede afectar la autenticación)
 * - No incluimos la contraseña aquí (debería tener su propio endpoint)
 * - Solo campos que el usuario puede modificar en su perfil
 */
@Data
@Builder
public class UserUpdateDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    @Email(regexp = ".*@.*\\..*", message = "Email debe ser válido")
    @NotBlank(message = "El email no puede estar vacío")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;
}
