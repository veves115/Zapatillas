package es.pabloab.zapatillas.rest.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ClienteUpdateDto {
    @Size(max = 100)
    private final String nombre;

    @Email(message = "Email no válido")
    @Size(max = 150)
    private final String email;
}
