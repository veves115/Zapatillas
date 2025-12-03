package es.pabloab.zapatilass.websockets.notifications.dto;

public record ZapatillaNotificationResponse(
        Long id,
        String marca,
        String modelo,
        String codigoProducto,
        Double talla,
        Double precio,
        String color,
        String tipo,
        Integer stock

        String createdAt,
        String updatedAt,
        String uuid,
        String string) {
}
