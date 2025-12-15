package es.pabloab.zapatillas.cliente.exceptions;

public class UsuarioNotFoundException extends UsuarioException {
    public UsuarioNotFoundException(Long id) {
        super("Usuario no encontrado id=" + id);
    }
}
