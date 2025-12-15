package es.pabloab.zapatillas.cliente.exceptions;

public class ClienteBadRequestException extends RuntimeException {
    public ClienteBadRequestException(String message) {
        super(message);
    }
}
