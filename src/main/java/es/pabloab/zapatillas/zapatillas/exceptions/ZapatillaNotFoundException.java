package es.pabloab.zapatillas.zapatillas.exceptions;

public class ZapatillaNotFoundException extends ZapatillaException {
    public ZapatillaNotFoundException(Long id) {
        super("No se encontro la zapatill con id=" + id);
    }

    public ZapatillaNotFoundException(String uuid) {
        super("No se encontro la zapatill con uuid=" + uuid);
    }
}
