package es.pabloab.zapatilass.zapatillas.exceptions;

public class ZapatillaBadUuidException extends ZapatillaException {
    public ZapatillaBadUuidException(String uuid) {
        super("El identificador '" + uuid + "' no es correcto.");
    }
}
