package es.pabloab.zapatillas.cliente.exceptions;

public class ClienteNotFoundException extends ClienteException {
    public ClienteNotFoundException(Long id) {
        super("Usuario no encontrado id=" + id);
    }
}
