package es.pabloab.zapatillas.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDto {
    private Long id;
    private String nombre;
    private String email;
}
