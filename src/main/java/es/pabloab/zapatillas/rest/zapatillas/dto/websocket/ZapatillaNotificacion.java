package es.pabloab.zapatillas.rest.zapatillas.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZapatillaNotificacion {

    private TipoNotificacion tipo;
    private Long zapatillaId;
    private String mensaje;
    private Object data;
    private LocalDateTime timestamp;

    public static ZapatillaNotificacion crear(TipoNotificacion tipo, Long zapatillaId, String mensaje, Object data) {
        return ZapatillaNotificacion.builder()
                .tipo(tipo)
                .zapatillaId(zapatillaId)
                .mensaje(mensaje)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public enum TipoNotificacion {
        CREATED("Zapatilla creada"),
        UPDATED("Zapatilla actualizada"),
        DELETED("Zapatilla eliminada"),
        STOCK_LOW("Stock bajo"),
        PRICE_CHANGED("Precio cambiado");

        private final String descripcion;

        TipoNotificacion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
}

