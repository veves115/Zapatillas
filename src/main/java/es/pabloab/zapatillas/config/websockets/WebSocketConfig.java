package es.pabloab.zapatillas.config.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker  // ← Habilita WebSocket con STOMP
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el Message Broker
     * <p>
     * Message Broker: Intermediario que gestiona los mensajes
     * - Recibe mensajes de clientes
     * - Los distribuye a los suscritos
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        /**
         * enableSimpleBroker: Habilita un broker IN-MEMORY simple
         *
         * Prefijos para ENVIAR mensajes a clientes:
         * - /topic: Para broadcast (uno a muchos)
         *   Ejemplo: /topic/zapatillas → Todos los suscritos reciben
         *
         * - /queue: Para mensajes privados (uno a uno)
         *   Ejemplo: /queue/usuario123 → Solo ese usuario recibe
         */
        config.enableSimpleBroker("/topic", "/queue");

        /**
         * setApplicationDestinationPrefixes: Prefijo para mensajes DESDE clientes
         *
         * Los clientes envían mensajes a rutas que empiezan con /app
         * Ejemplo: Cliente envía a "/app/zapatillas/actualizar"
         *         → Spring lo procesa en @MessageMapping("/zapatillas/actualizar")
         */
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra endpoints de conexión WebSocket
     * <p>
     * Endpoint: URL donde los clientes se conectan inicialmente
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        /**
         * addEndpoint: Define el punto de conexión
         *
         * URL: ws://localhost:8080/ws
         *
         * Los clientes JavaScript se conectan así:
         * const socket = new SockJS('http://localhost:8080/ws');
         */
        registry.addEndpoint("/ws")

                /**
                 * setAllowedOriginPatterns: Permite CORS
                 *
                 * "*": Permite conexiones desde cualquier origen
                 *
                 * En producción, especifica el dominio exacto:
                 * .setAllowedOrigins("https://mi-dominio.com")
                 */
                .setAllowedOriginPatterns("*")

                /**
                 * withSockJS: Habilita fallback a SockJS
                 *
                 * SockJS: Si WebSocket no está disponible, usa:
                 * - HTTP long-polling
                 * - HTTP streaming
                 * - etc.
                 *
                 * Garantiza compatibilidad con navegadores antiguos
                 */
                .withSockJS();
    }
}