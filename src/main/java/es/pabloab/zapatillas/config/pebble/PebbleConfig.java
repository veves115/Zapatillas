package es.pabloab.zapatillas.config.pebble;

import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Configuración de extensiones personalizadas para Pebble.
 *
 * ¿QUÉ SON LOS FILTROS DE PEBBLE?
 * =================================
 * Los filtros transforman valores en las plantillas. Se usan con el pipe (|):
 *   {{ variable | nombreFiltro }}
 *
 * Pebble incluye filtros como | upper, | lower, | default('').
 * Aquí creamos filtros propios para nuestro dominio:
 *
 * FILTROS REGISTRADOS:
 * - formatDate     → "12/02/2026"
 * - formatPrice    → "89,99 €"
 * - formatDateTime → "12 feb 2026, 20:56"
 *
 * ¿CÓMO SE REGISTRAN?
 * Creamos una clase que extiende AbstractExtension y sobreescribe getFilters().
 * Spring Boot la registra automáticamente si la declaramos como @Bean.
 *
 * EJEMPLO DE USO EN PLANTILLA:
 *   {{ zapatilla.precio | formatPrice }}       → "89,99 €"
 *   {{ zapatilla.createdAt | formatDate }}     → "12/02/2026"
 *   {{ zapatilla.createdAt | formatDateTime }} → "12 feb 2026, 20:56"
 */
@Configuration
public class PebbleConfig {

    private static final Locale LOCALE_ES = new Locale("es", "ES");

    @Bean
    public AbstractExtension customPebbleExtension() {
        return new AbstractExtension() {
            @Override
            public Map<String, Filter> getFilters() {
                return Map.of(
                        "formatDate", new FormatDateFilter(),
                        "formatPrice", new FormatPriceFilter(),
                        "formatDateTime", new FormatDateTimeFilter()
                );
            }
        };
    }

    // =========================================================================
    // FILTRO: formatDate
    // =========================================================================
    // Convierte LocalDateTime o LocalDate a formato "dd/MM/yyyy"
    // Ejemplo: 2026-02-12T20:56:18 → "12/02/2026"
    //
    // ¿Por qué comprobamos el tipo con instanceof?
    // Porque el valor puede ser LocalDateTime (tiene fecha+hora) o
    // LocalDate (solo fecha). Manejamos ambos casos.
    static class FormatDateFilter implements Filter {
        private static final DateTimeFormatter FORMATTER =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        @Override
        public Object apply(Object input, Map<String, Object> args,
                            PebbleTemplate self, EvaluationContext context, int lineNumber) {
            if (input instanceof LocalDateTime ldt) {
                return ldt.format(FORMATTER);
            } else if (input instanceof LocalDate ld) {
                return ld.format(FORMATTER);
            }
            return input != null ? input.toString() : "";
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }

    // =========================================================================
    // FILTRO: formatPrice
    // =========================================================================
    // Formatea un número como precio en euros con formato europeo.
    // Ejemplo: 89.99 → "89,99 €"
    //
    // Usamos String.format con Locale español para que use coma decimal
    // en vez de punto (convención europea).
    static class FormatPriceFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args,
                            PebbleTemplate self, EvaluationContext context, int lineNumber) {
            if (input instanceof Number number) {
                return String.format(LOCALE_ES, "%,.2f €", number.doubleValue());
            }
            return input != null ? input.toString() : "";
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }

    // =========================================================================
    // FILTRO: formatDateTime
    // =========================================================================
    // Convierte LocalDateTime a formato legible "d MMM yyyy, HH:mm"
    // Ejemplo: 2026-02-12T20:56:18 → "12 feb 2026, 20:56"
    //
    // Usamos Locale español para que los meses salgan en castellano.
    static class FormatDateTimeFilter implements Filter {
        private static final DateTimeFormatter FORMATTER =
                DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", LOCALE_ES);

        @Override
        public Object apply(Object input, Map<String, Object> args,
                            PebbleTemplate self, EvaluationContext context, int lineNumber) {
            if (input instanceof LocalDateTime ldt) {
                return ldt.format(FORMATTER);
            }
            return input != null ? input.toString() : "";
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }
}
