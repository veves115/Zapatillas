package es.pabloab.zapatilass.dto;

import es.pabloab.zapatilass.validators.CodigoProducto;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
@Builder
@Data

public class ZapatillaCreateDto {
    @NotBlank(message = "La marca no puede estar vacío")
    private final String marca;

    @NotBlank(message = "El modelo no puede estar vacío")
    private final String modelo;

    @CodigoProducto
    private final String codigoProducto;

    @NotNull(message = "La talla es obligatoria")
    @DecimalMin(value = "20.0", message = "La talla mínima es 20")
    @DecimalMax(value = "52.0", message = "La talla máxima es 52")
    private final Double talla;

    @NotBlank(message = "El color no puede estar vacío")
    private final String color;

    @NotBlank(message = "El tipo no puede estar vacío")
    @Pattern(regexp = "Running|Basketball|Training|Casual|Trail",
            message = "El tipo debe de ser: Running, Basketball, Training, Casual o Trail")
    private final String tipo;

    @NotNull(message = "El precio no puede estar vacío")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private final Double precio;

    @NotNull(message = "El stock no puede estar vacío")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private final Integer stock;




}

