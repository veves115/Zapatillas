package es.pabloab.zapatilass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZapatillaResponseDto {
    private Long id;
    private String marca;
    private String modelo;
    private String codigoProducto;
    private Double talla;
    private String color;
    private String tipo;
    private Double precio;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID uuid;
}

