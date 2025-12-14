package es.pabloab.zapatillas.usuarios.exceptions;

public class UsuarioNotFoundException extends UsuarioException {
    public UsuarioNotFoundException(Long id) {
        super("Usuario no encontrado id=" + id);
    }
}
