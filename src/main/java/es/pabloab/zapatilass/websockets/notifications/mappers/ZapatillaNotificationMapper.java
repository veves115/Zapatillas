package es.pabloab.zapatilass.websockets.notifications.mappers;

import es.pabloab.zapatilass.websockets.notifications.dto.ZapatillaNotificationResponse;
import es.pabloab.zapatilass.zapatillas.models.Zapatilla;
import org.springframework.stereotype.Component;

@Component
public class ZapatillaNotificationMapper {
    public ZapatillaNotificationResponse toTarjetaNotificationDto(Zapatilla zapatilla) {
        return new ZapatillaNotificationResponse(
                zapatilla.getId(),
                zapatilla.getMarca(),
                zapatilla.getModelo(),
                zapatilla.getCodigoProducto(),
                zapatilla.getTalla(),
                zapatilla.getPrecio(),
                zapatilla.getColor(),
                zapatilla.getTipo(),
                zapatilla.getStock(),
                zapatilla.getCreatedAt() != null ? zapatilla.getCreatedAt().toString() : null,
                zapatilla.getUpdatedAt() != null ? zapatilla.getUpdatedAt().toString() : null,
                zapatilla.getUuid() != null ? zapatilla.getUuid().toString() : null
        );
    }
}

