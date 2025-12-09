package es.pabloab.zapatillas.zapatillas.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
public class ZapatillaUpdateDto {
    private final String codigoProducto;

    @DecimalMin(value = "20.0", message = "La talla mínima es 20")
    @DecimalMax(value = "52.0", message = "La talla máxima es 52")
    private final Double talla;
    private final String color;

    @Pattern(regexp = "Running|Basketball|Training|Casual|Trail",
            message = "Tipo debe ser: Running, Basketball, Training, Casual o Trail")
    private final String tipo;

    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private final Double precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private final Integer stock;
}
