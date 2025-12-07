package es.pabloab.zapatillas.zapatillas.validators;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

/**
 * Clase de prueba para verificar que Jakarta Validation funciona
 * Ejecuta el main() para comprobar que todo está configurado correctamente
 */
public class ValidationTest {

    @Data
    static class TestDto {
        @NotBlank(message = "El nombre no puede estar vacío")
        private String nombre;

        @Min(value = 0, message = "La edad no puede ser negativa")
        private Integer edad;
    }

    public static void main(String[] args) {
        System.out.println("=== Test de Jakarta Validation ===\n");

        // Crear el validador
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // === TEST 1: DTO VÁLIDO ===
        System.out.println("TEST 1: DTO válido");
        TestDto dtoValido = new TestDto();
        dtoValido.setNombre("Carlos");
        dtoValido.setEdad(25);

        Set<ConstraintViolation<TestDto>> violationsValido = validator.validate(dtoValido);

        if (violationsValido.isEmpty()) {
            System.out.println("✅ DTO válido - No hay errores\n");
        } else {
            System.out.println("❌ ERROR: DTO válido tiene errores\n");
        }

        // === TEST 2: DTO INVÁLIDO ===
        System.out.println("TEST 2: DTO inválido");
        TestDto dtoInvalido = new TestDto();
        dtoInvalido.setNombre("");  // Vacío
        dtoInvalido.setEdad(-5);     // Negativo

        Set<ConstraintViolation<TestDto>> violationsInvalido = validator.validate(dtoInvalido);

        if (!violationsInvalido.isEmpty()) {
            System.out.println("✅ Se detectaron " + violationsInvalido.size() + " errores:");
            violationsInvalido.forEach(v ->
                    System.out.println("  - " + v.getPropertyPath() + ": " + v.getMessage())
            );
        } else {
            System.out.println("❌ ERROR: DTO inválido no tiene errores");
        }

        System.out.println("\n=== Jakarta Validation funciona correctamente ===");
    }
}

