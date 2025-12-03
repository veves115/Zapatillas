package es.pabloab.zapatilass.dto;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Validaciones de ZapatillaCreateDto")
class ZapatillaCreateDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ZapatillaCreateDto createValidDto() {
        return ZapatillaCreateDto.builder()
                .marca("Nike")
                .modelo("Air Max 90")
                .codigoProducto("NI1234KE")
                .talla(42.0)
                .color("Blanco")
                .tipo("Running")
                .precio(129.99)
                .stock(10)
                .build();
    }

    @Test
    @DisplayName("DTO con todos los campos válidos no debe tener errores")
    void dtoValidoNoTieneErrores() {
        // GIVEN
        ZapatillaCreateDto dto = createValidDto();

        // WHEN - Validar el DTO
        Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

        // THEN - No debe haber errores
        assertThat(violations).isEmpty();
    }

    @Nested
    @DisplayName("Validaciones del campo 'marca'")
    class MarcaValidationTests {

        @Test
        @DisplayName("Marca null debe generar error")
        void marcaNull() {
            // GIVEN
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca(null)  // ← INVÁLIDO
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations).hasSize(1);

            ConstraintViolation<ZapatillaCreateDto> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("marca");
            assertThat(violation.getMessage()).contains("marca");
        }

        @Test
        @DisplayName("Marca vacía debe generar error")
        void marcaVacia() {
            ZapatillaCreateDto dto = createValidDto();
            dto = ZapatillaCreateDto.builder()
                    .marca("")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("marca");
        }

        @Test
        @DisplayName("Marca con solo espacios debe generar error")
        void marcaSoloEspacios() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("   ")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);
            assertThat(violations).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Validaciones del campo 'modelo'")
    class ModeloValidationTests {

        @Test
        @DisplayName("Modelo null debe generar error")
        void modeloNull() {
            // GIVEN
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo(null)  // ← INVÁLIDO
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("modelo");
        }

        @Test
        @DisplayName("Modelo vacío debe generar error")
        void modeloVacio() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            // THEN
            assertThat(violations).isNotEmpty();
        }
    }

    // ==========================================
    // TESTS DE TALLA
    // ==========================================

    @Nested
    @DisplayName("Validaciones del campo 'talla'")
    class TallaValidationTests {

        @Test
        @DisplayName("Talla null debe generar error")
        void tallaNull() {
            // GIVEN
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(null)  // ← INVÁLIDO
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();

            // WHEN
            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            // THEN
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("talla");
        }

        @Test
        @DisplayName("Talla menor a 20 debe generar error")
        void tallaMuyPequena() {
            // GIVEN
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(10.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();
            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);
            assertThat(violations).isNotEmpty();

            ConstraintViolation<ZapatillaCreateDto> violation = violations.stream()
                    .filter(v -> v.getPropertyPath().toString().equals("talla"))
                    .findFirst()
                    .orElseThrow();

            assertThat(violation.getMessage()).contains("mínima es 20");
        }

        @Test
        @DisplayName("Talla mayor a 52 debe generar error")
        void tallaMuyGrande() {
            // GIVEN
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(60.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().equals("talla"));
        }

        @Test
        @DisplayName("Talla en el límite inferior (20.0) debe ser válida")
        void tallaLimiteInferior() {
            // GIVEN
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(20.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();
            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations)
                    .noneMatch(v -> v.getPropertyPath().toString().equals("talla"));
        }

        @Test
        @DisplayName("Talla en el límite superior (52.0) debe ser válida")
        void tallaLimiteSuperior() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(52.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();
            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);
            assertThat(violations)
                    .noneMatch(v -> v.getPropertyPath().toString().equals("talla"));
        }
    }

    @Nested
    @DisplayName("Validaciones del campo 'precio'")
    class PrecioValidationTests {

        @Test
        @DisplayName("Precio null debe generar error")
        void precioNull() {

            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(null)
                    .stock(10)
                    .build();
            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Precio negativo debe generar error")
        void precioNegativo() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(-50.0)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().equals("precio"));
        }

        @Test
        @DisplayName("Precio cero debe generar error")
        void precioCero() {
            // GIVEN
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(0.0)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Precio 0.01 (límite inferior) debe ser válido")
        void precioLimiteInferior() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(0.01)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);
            assertThat(violations)
                    .noneMatch(v -> v.getPropertyPath().toString().equals("precio"));
        }
    }

    @Nested
    @DisplayName("Validaciones del campo 'stock'")
    class StockValidationTests {

        @Test
        @DisplayName("Stock null debe generar error")
        void stockNull() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(null)
                    .build();
            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Stock negativo debe generar error")
        void stockNegativo() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(-10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().equals("stock"));
        }

        @Test
        @DisplayName("Stock cero debe ser válido")
        void stockCero() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(0)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations)
                    .noneMatch(v -> v.getPropertyPath().toString().equals("stock"));
        }
    }

    @Nested
    @DisplayName("Validaciones del campo 'tipo'")
    class TipoValidationTests {

        @Test
        @DisplayName("Tipo null debe generar error")
        void tipoNull() {

            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo(null)
                    .precio(129.99)
                    .stock(10)
                    .build();
            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Tipo vacío debe generar error")
        void tipoVacio() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("")
                    .precio(129.99)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Tipo inválido debe generar error")
        void tipoInvalido() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Natacion")
                    .precio(129.99)
                    .stock(10)
                    .build();
            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().equals("tipo"));
        }

        @Test
        @DisplayName("Tipos válidos no deben generar error")
        void tiposValidos() {
            String[] tiposValidos = {"Running", "Basketball", "Training", "Casual", "Trail"};
            for (String tipo : tiposValidos) {
                ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                        .marca("Nike")
                        .modelo("Air Max")
                        .codigoProducto("NI1234KE")
                        .talla(42.0)
                        .color("Blanco")
                        .tipo(tipo)
                        .precio(129.99)
                        .stock(10)
                        .build();

                Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

                assertThat(violations)
                        .as("El tipo '%s' debería ser válido", tipo)
                        .noneMatch(v -> v.getPropertyPath().toString().equals("tipo"));
            }
        }
    }

    @Nested
    @DisplayName("Validaciones del campo 'color'")
    class ColorValidationTests {

        @Test
        @DisplayName("Color null debe generar error")
        void colorNull() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color(null)
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Color vacío debe generar error")
        void colorVacio() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();

            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Tests con múltiples errores")
    class MultipleErrorsTests {

        @Test
        @DisplayName("DTO completamente inválido debe generar múltiples errores")
        void dtoCompleInvalido() {
            ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                    .marca("")
                    .modelo("")
                    .codigoProducto("INVALIDO")
                    .talla(10.0)
                    .color("")
                    .tipo("Invalido")
                    .precio(-50.0)
                    .stock(-10)
                    .build();
            Set<ConstraintViolation<ZapatillaCreateDto>> violations = validator.validate(dto);

            assertThat(violations).hasSizeGreaterThanOrEqualTo(5);

            Set<String> camposConError = violations.stream()
                    .map(v -> v.getPropertyPath().toString())
                    .collect(java.util.stream.Collectors.toSet());

            assertThat(camposConError).contains("marca", "modelo", "precio", "stock");
        }
    }
}
