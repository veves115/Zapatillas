# Websockets

El proyecto usa STOMP sobre WebSocket con **SimpleMessageTemplate** para enviar notificaciones al topic '/topic/zapatillas'.
El servicio (**'ZapatillasServiceImpl.save','update','deleyeById'** crea objetos de notificación y llama a métodos del 
controller WebSocket para enviar mensajes)

### Requisitos previos

- Java 17+ / OpenJDK instalado 
- Maven wrapper
- Navegador con consola JS o cliente STOMP/SockJs para pruebas 

### Como probar

- Enviando un Post Rest para enviar una notificación
- Usa `crud-zapatillas.http` o una herramienta como Postman

--------
Construye una comunicación bidireccional con una conexión persistente que no se cierra hasta que el cliente lo solicita
pero el servidor puede enviar comunicaciones sin que el cliente lo pida lo cual ayuda a preservar una lectura clara de 
la aplicación en todo lo que sea necesario y sobre todo en tiempo real, sin esperas de determinados procesos

En el caso del servicio de zapatillas está pensado para que al comprarse una zapatilla el stock,precio...etc cambie y el resto de 
clientes sean conscientes de ello a través de una notificación,