package es.pabloab.zapatillas.zapatillas.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para notificaciones de WebSocket sobre zapatillas
 * <p>
 * Tipos de notificaciones:
 * - CREATED: Nueva zapatilla creada
 * - UPDATED: Zapatilla actualizada
 * - DELETED: Zapatilla eliminada
 * - STOCK_LOW: Stock bajo
 * - PRICE_CHANGED: Precio cambiado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZapatillaNotificacion {

    /**
     * Tipo de notificación
     */
    private TipoNotificacion tipo;

    /**
     * ID de la zapatilla afectada
     */
    private Long zapatillaId;

    /**
     * Mensaje descriptivo
     */
    private String mensaje;

    /**
     * Datos de la zapatilla (opcional)
     */
    private Object data;

    /**
     * Timestamp de la notificación
     */
    private LocalDateTime timestamp;

    /**
     * Constructor de conveniencia
     */
    public static ZapatillaNotificacion crear(TipoNotificacion tipo, Long zapatillaId, String mensaje, Object data) {
        return ZapatillaNotificacion.builder()
                .tipo(tipo)
                .zapatillaId(zapatillaId)
                .mensaje(mensaje)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Enum con tipos de notificación
     */
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


/**
 * DTO para mensajes de chat (opcional)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MensajeChat {
    private String usuario;
    private String mensaje;
    private LocalDateTime timestamp;
}


/**
 * DTO para estadísticas en tiempo real (opcional)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EstadisticasZapatillas {
    private Long totalZapatillas;
    private Long stockTotal;
    private Double precioPromedio;
    private LocalDateTime timestamp;
}
