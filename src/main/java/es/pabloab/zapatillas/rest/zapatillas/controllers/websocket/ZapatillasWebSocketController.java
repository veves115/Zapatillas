package es.pabloab.zapatillas.rest.zapatillas.controllers.websocket;

import es.pabloab.zapatillas.rest.zapatillas.dto.websocket.ZapatillaNotificacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ZapatillasWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/zapatillas/test")
    @SendTo("/topic/zapatillas")
    public ZapatillaNotificacion handleTest(String mensaje) {
        log.info("WebSocket: Mensaje recibido: {}", mensaje);

        return ZapatillaNotificacion.crear(
                ZapatillaNotificacion.TipoNotificacion.CREATED,
                null,
                "Test de WebSocket: " + mensaje,
                null
        );
    }
    public void enviarNotificacion(ZapatillaNotificacion notificacion) {
        log.info("WebSocket: Enviando notificación tipo={} id={}",
                notificacion.getTipo(), notificacion.getZapatillaId());

        messagingTemplate.convertAndSend("/topic/zapatillas", notificacion);
    }

    public void enviarNotificacionPrivada(String userId, ZapatillaNotificacion notificacion) {
        log.info("WebSocket: Enviando notificación privada a usuario={}", userId);

        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notificaciones",
                notificacion
        );

    }
    public void enviarATodos(String mensaje) {
        log.info("WebSocket: Broadcast a todos: {}", mensaje);

        messagingTemplate.convertAndSend(
                "/topic/broadcast",
                mensaje
        );
    }
    public void notificarCambioStock(Long zapatillaId, Integer nuevoStock) {
        ZapatillaNotificacion notificacion = ZapatillaNotificacion.crear(
                ZapatillaNotificacion.TipoNotificacion.UPDATED,
                zapatillaId,
                "Stock actualizado a: " + nuevoStock,
                nuevoStock
        );

        enviarNotificacion(notificacion);
    }
    public void notificarStockBajo(Long zapatillaId, Integer stock) {
        if (stock < 5) {
            ZapatillaNotificacion notificacion = ZapatillaNotificacion.crear(
                    ZapatillaNotificacion.TipoNotificacion.STOCK_LOW,
                    zapatillaId,
                    "¡Atención! Solo quedan " + stock + " unidades",
                    stock
            );

            enviarNotificacion(notificacion);
        }
    }
}
