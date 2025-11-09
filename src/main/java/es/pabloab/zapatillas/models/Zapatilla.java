package es.pabloab.zapatillas.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class Zapatilla {
    private Long id;

    private String marca;
    private String modelo;
    private String codigoProducto;
    private Double talla;
    private Double precio;
    private String color;
    private String tipo;
    private Integer stock;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID uuid;
}
