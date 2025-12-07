package es.pabloab.zapatillas.zapatillas.validators;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CodigoProductoValidator implements ConstraintValidator<CodigoProducto, String> {
    @Override
    public void initialize(CodigoProducto codigoProducto) {
    }

    @Override
    public boolean isValid(String codigo, ConstraintValidatorContext context) {
        if (codigo == null) {
            return true; // Otras anotaciones controlan null
        }
        // Formato: 2 letras + 4 dígitos + 2 letras (ej: NI1234KE)
        return codigo.matches("[A-Z]{2}[0-9]{4}[A-Z]{2}");
    }
}

