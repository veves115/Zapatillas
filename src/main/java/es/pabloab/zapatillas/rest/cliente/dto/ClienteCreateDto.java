package es.pabloab.zapatillas.rest.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ClienteCreateDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100)
    private final String nombre;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Email no válido")
    @Size(max = 150)
    private final String email;

}
