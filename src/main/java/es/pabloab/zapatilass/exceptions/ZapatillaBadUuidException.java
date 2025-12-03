package es.pabloab.zapatilass.exceptions;

public class ZapatillaBadUuidException extends ZapatillaException {
    public ZapatillaBadUuidException(String uuid) {
        super("El identificador '" + uuid + "' no es correcto.");
    }
}
