package es.pabloab.zapatilass.websockets.notifications.models;

public record Notificacion<Z>(
        String entity,
        Tipo type,
        Z data,
        String createdAt
) {

    public enum Tipo {CREATE, UPDATE, DELETE}

}
