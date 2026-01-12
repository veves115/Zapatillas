package es.pabloab.zapatillas.rest.cliente.exceptions;

public class ClienteBadRequestException extends RuntimeException {
    public ClienteBadRequestException(String message) {
        super(message);
    }
}
