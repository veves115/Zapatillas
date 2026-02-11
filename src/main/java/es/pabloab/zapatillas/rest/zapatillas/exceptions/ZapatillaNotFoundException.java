package es.pabloab.zapatillas.rest.zapatillas.exceptions;

public class ZapatillaNotFoundException extends ZapatillaException {
    public ZapatillaNotFoundException(Long id) {
        super("No se encontró la zapatilla con id=" + id);
    }

    public ZapatillaNotFoundException(String uuid) {
        super("No se encontró la zapatilla con uuid=" + uuid);
    }
}
