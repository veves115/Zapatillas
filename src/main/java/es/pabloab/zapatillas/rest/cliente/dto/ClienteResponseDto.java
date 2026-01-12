package es.pabloab.zapatillas.rest.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponseDto {
    private Long id;
    private String nombre;
    private String email;
}
