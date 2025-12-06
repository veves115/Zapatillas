package es.pabloab.zapatilass.websockets.notifications.services;

import es.pabloab.zapatilass.config.websockets.WebSocketSender;
import es.pabloab.zapatilass.websockets.notifications.dto.ZapatillaNotificationResponse;
import es.pabloab.zapatilass.websockets.notifications.mappers.ZapatillaNotificationMapper;
import es.pabloab.zapatilass.websockets.notifications.models.Notificacion;
import es.pabloab.zapatilass.zapatillas.models.Zapatilla;
import es.pabloab.zapatilass.zapatillas.models.ZapatillaChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final WebSocketSender webSocketSender;
    private final ZapatillaNotificationMapper notificationMapper;

    @Async
    @EventListener
    public void handleZapatillaChangeEvent(ZapatillaChangeEvent event) {
        log.info("Evento recibido: {} para zapatilla con id: {}", event.getChangeType(), event.getZapatilla().getId());
        
        try {
            Zapatilla zapatilla = event.getZapatilla();
            ZapatillaNotificationResponse notificationDto = notificationMapper.toTarjetaNotificationDto(zapatilla);
            
            Notificacion.Tipo tipo = switch (event.getChangeType()) {
                case CREATE -> Notificacion.Tipo.CREATE;
                case UPDATE -> Notificacion.Tipo.UPDATE;
                case DELETE -> Notificacion.Tipo.DELETE;
            };
            
            Notificacion<ZapatillaNotificationResponse> notification = new Notificacion<>(
                    "Zapatilla",
                    tipo,
                    notificationDto,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            
            // Serializar manualmente a JSON
            String jsonMessage = toJson(notification);
            webSocketSender.sendMessage(jsonMessage);
            
            log.info("Notificación enviada por WebSocket: {}", jsonMessage);
        } catch (IOException e) {
            log.error("Error al enviar la notificación por WebSocket", e);
        }
    }
    
    private String toJson(Notificacion<ZapatillaNotificationResponse> notification) {
        ZapatillaNotificationResponse data = notification.data();
        return String.format(
            "{\"entity\":\"%s\",\"type\":\"%s\",\"data\":{\"id\":%s,\"marca\":\"%s\",\"modelo\":\"%s\",\"codigoProducto\":\"%s\",\"talla\":%s,\"precio\":%s,\"color\":\"%s\",\"tipo\":\"%s\",\"stock\":%s,\"createdAt\":\"%s\",\"updatedAt\":\"%s\",\"uuid\":\"%s\"},\"createdAt\":\"%s\"}",
            notification.entity(),
            notification.type().name(),
            data.id() != null ? data.id() : "null",
            escapeJson(data.marca()),
            escapeJson(data.modelo()),
            escapeJson(data.codigoProducto()),
            data.talla() != null ? data.talla() : "null",
            data.precio() != null ? data.precio() : "null",
            escapeJson(data.color()),
            escapeJson(data.tipo()),
            data.stock() != null ? data.stock() : "null",
            data.createdAt() != null ? data.createdAt() : "",
            data.updatedAt() != null ? data.updatedAt() : "",
            data.uuid() != null ? data.uuid() : "",
            notification.createdAt()
        );
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}

