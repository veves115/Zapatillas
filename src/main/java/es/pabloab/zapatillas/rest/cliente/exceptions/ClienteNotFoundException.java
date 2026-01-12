package es.pabloab.zapatillas.rest.cliente.exceptions;

public class ClienteNotFoundException extends ClienteException {
    public ClienteNotFoundException(Long id) {
        super("Usuario no encontrado id=" + id);
    }
}
