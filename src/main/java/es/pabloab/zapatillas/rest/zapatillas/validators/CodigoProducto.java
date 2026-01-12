package es.pabloab.zapatillas.rest.zapatillas.validators;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CodigoProductoValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CodigoProducto {
    String message() default "El código de producto no es válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

