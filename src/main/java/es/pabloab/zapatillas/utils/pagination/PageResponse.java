package es.pabloab.zapatillas.utils.pagination;

import org.springframework.data.domain.Page;
import java.util.List;

/**
 * Record que envuelve un Page<T> de Spring Data en una respuesta limpia para el cliente.
 *
 * ¿Por qué un record? Porque solo necesitamos transportar datos, sin lógica.
 * Java genera automáticamente constructor, getters, equals, hashCode y toString.
 *
 * @param <T> El tipo de los elementos (ej: ZapatillaResponseDto)
 */
public record PageResponse<T>(
        List<T> content,          // Los elementos de esta página
        int totalPages,           // Total de páginas disponibles
        long totalElements,       // Total de elementos en BBDD
        int pageSize,             // Tamaño de página solicitado
        int pageNumber,           // Número de página actual (0-indexed)
        int totalPageElements,    // Elementos en ESTA página
        boolean empty,            // ¿Está vacía?
        boolean first,            // ¿Es la primera página?
        boolean last,             // ¿Es la última página?
        String sortBy,            // Campo por el que se ordena
        String direction          // ASC o DESC
) {
    /**
     * Factory method: convierte un Page<T> de Spring en nuestro PageResponse<T>.
     *
     * Se usa así: PageResponse.of(page, "marca", "ASC")
     */
    public static <T> PageResponse<T> of(Page<T> page, String sortBy, String direction) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber(),
                page.getNumberOfElements(),
                page.isEmpty(),
                page.isFirst(),
                page.isLast(),
                sortBy,
                direction
        );
    }
}
