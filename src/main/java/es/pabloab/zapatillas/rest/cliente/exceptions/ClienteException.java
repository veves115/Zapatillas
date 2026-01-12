package es.pabloab.zapatillas.rest.cliente.exceptions;

public class ClienteException extends Exception {
    public ClienteException(String message) {
        super(message);
    }

    public ClienteException(String message, Throwable causa){
        super(message,causa);
    }
}
