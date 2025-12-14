package es.pabloab.zapatillas.usuarios.exceptions;

public class UsuarioBadRequestException extends RuntimeException {
    public UsuarioBadRequestException(String message) {
        super(message);
    }
}
