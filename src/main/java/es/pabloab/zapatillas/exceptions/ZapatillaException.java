package es.pabloab.zapatillas.exceptions;

public abstract class ZapatillaException extends Exception {
    public ZapatillaException(String mensaje) {
        super(mensaje);
    }
}
