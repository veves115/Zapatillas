package es.pabloab.zapatillas.zapatillas.controllers.websocket;

import es.pabloab.zapatillas.zapatillas.dto.websocket.ZapatillaNotificacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Controller para WebSockets
 * <p>
 * ANOTACIONES:
 *
 * @Controller (no @ RestController): Para WebSocket
 * @MessageMapping: Define qué mensajes procesa (como @PostMapping)
 * @SendTo: Define dónde envía la respuesta
 * <p>
 * FLUJO:
 * 1. Cliente envía mensaje a /app/zapatillas/test
 * 2. Spring llama a handleTest()
 * 3. Método procesa y devuelve
 * 4. Spring envía resultado a /topic/zapatillas
 * 5. Todos los suscritos a /topic/zapatillas reciben el mensaje
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ZapatillasWebSocketController {

    /**
     * SimpMessagingTemplate: Herramienta para ENVIAR mensajes
     * <p>
     * Se usa para enviar mensajes desde cualquier parte del código
     * (no solo desde @MessageMapping)
     */
    private final SimpMessagingTemplate messagingTemplate;

    // ==========================================
    // EJEMPLO 1: RECIBIR Y RESPONDER
    // ==========================================

    /**
     * Procesa mensajes del cliente y responde
     *
     * @MessageMapping("/zapatillas/test") - Cliente envía a: /app/zapatillas/test
     * - Spring ejecuta este método
     * @SendTo("/topic/zapatillas") - La respuesta se envía a: /topic/zapatillas
     * - Todos los suscritos reciben
     * <p>
     * FLUJO COMPLETO:
     * Cliente → /app/zapatillas/test → handleTest() → /topic/zapatillas → Todos
     */
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

    // ==========================================
    // EJEMPLO 2: ENVIAR DESDE CUALQUIER PARTE DEL CÓDIGO
    // ==========================================

    /**
     * Método para enviar notificaciones desde el Service
     * <p>
     * Este método NO es un @MessageMapping
     * Se llama directamente desde el código (ej: después de crear una zapatilla)
     * <p>
     * USO:
     * webSocketController.enviarNotificacion(notificacion);
     */
    public void enviarNotificacion(ZapatillaNotificacion notificacion) {
        log.info("WebSocket: Enviando notificación tipo={} id={}",
                notificacion.getTipo(), notificacion.getZapatillaId());

        /**
         * convertAndSend: Envía un mensaje a un destino
         *
         * Parámetros:
         * 1. Destino: /topic/zapatillas
         * 2. Objeto: Se convierte a JSON automáticamente
         */
        messagingTemplate.convertAndSend("/topic/zapatillas", notificacion);
    }

    // ==========================================
    // EJEMPLO 3: MENSAJES PRIVADOS A UN USUARIO
    // ==========================================

    /**
     * Envía notificación a un usuario específico
     *
     * @param userId       ID del usuario
     * @param notificacion Notificación a enviar
     *                     <p>
     *                     El usuario debe estar suscrito a: /queue/notificaciones/{userId}
     */
    public void enviarNotificacionPrivada(String userId, ZapatillaNotificacion notificacion) {
        log.info("WebSocket: Enviando notificación privada a usuario={}", userId);

        messagingTemplate.convertAndSendToUser(
                userId,                           // Usuario destino
                "/queue/notificaciones",          // Destino
                notificacion                      // Mensaje
        );
        // El usuario recibirá en: /user/queue/notificaciones
    }

    // ==========================================
    // EJEMPLO 4: BROADCAST A TODOS
    // ==========================================

    /**
     * Envía un mensaje a TODOS los conectados
     */
    public void enviarATodos(String mensaje) {
        log.info("WebSocket: Broadcast a todos: {}", mensaje);

        messagingTemplate.convertAndSend(
                "/topic/broadcast",
                mensaje
        );
    }

    // ==========================================
    // EJEMPLO 5: NOTIFICAR CAMBIO DE STOCK
    // ==========================================

    /**
     * Notifica cuando el stock de una zapatilla cambia
     */
    public void notificarCambioStock(Long zapatillaId, Integer nuevoStock) {
        ZapatillaNotificacion notificacion = ZapatillaNotificacion.crear(
                ZapatillaNotificacion.TipoNotificacion.UPDATED,
                zapatillaId,
                "Stock actualizado a: " + nuevoStock,
                nuevoStock
        );

        enviarNotificacion(notificacion);
    }

    // ==========================================
    // EJEMPLO 6: NOTIFICAR STOCK BAJO
    // ==========================================

    /**
     * Notifica cuando el stock está bajo
     */
    public void notificarStockBajo(Long zapatillaId, Integer stock) {
        if (stock < 5) {  // Umbral de stock bajo
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
