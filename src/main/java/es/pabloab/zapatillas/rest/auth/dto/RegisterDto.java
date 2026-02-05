package es.pabloab.zapatillas.rest.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * DTO para solicitud de registro de usuario.
 *
 * IMPORTANTE:
 * - Incluye validaciones con anotaciones de Jakarta Validation
 * - La password y passwordComprobacion deben coincidir (validado en el servicio)
 * - Mínimo 6 caracteres para la contraseña
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    private String apellidos;

    @NotBlank(message = "El username no puede estar vacío")
    private String username;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(regexp = ".*@.*\\..*", message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Length(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "La confirmación de contraseña no puede estar vacía")
    private String passwordComprobacion;
}