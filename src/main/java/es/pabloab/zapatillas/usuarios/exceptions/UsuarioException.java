package es.pabloab.zapatillas.usuarios.exceptions;

public class UsuarioException extends Exception {
    public UsuarioException(String message) {
        super(message);
    }

    public UsuarioException(String message,Throwable causa){
        super(message,causa);
    }
}
