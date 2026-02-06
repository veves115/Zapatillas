package es.pabloab.zapatillas.utils.pagination;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Genera cabeceras Link (RFC 8288) para navegación entre páginas.
 *
 * RFC 8288 define relaciones estándar:
 * - next: siguiente página
 * - prev: página anterior
 * - first: primera página
 * - last: última página
 *
 * @Component para que Spring lo inyecte donde lo necesitemos.
 */
@Component
public class PaginationLinksUtils {

    /**
     * Construye el valor de la cabecera Link a partir del Page y la URL base.
     *
     * @param page    El resultado paginado de Spring Data
     * @param uriBuilder Constructor de URI que contiene la URL de la petición actual
     * @return String con los links separados por comas, listo para la cabecera
     */
    public String createLinkHeader(Page<?> page, UriComponentsBuilder uriBuilder) {
        final StringBuilder linkHeader = new StringBuilder();

        // Si NO es la última página → hay "next"
        if (page.hasNext()) {
            String uri = buildUri(uriBuilder, page.getNumber() + 1, page.getSize());
            linkHeader.append(buildLinkEntry(uri, "next"));
        }

        // Si NO es la primera página → hay "prev"
        if (page.hasPrevious()) {
            String uri = buildUri(uriBuilder, page.getNumber() - 1, page.getSize());
            if (!linkHeader.isEmpty()) linkHeader.append(", ");
            linkHeader.append(buildLinkEntry(uri, "prev"));
        }

        // Siempre incluimos "first" y "last"
        // First = página 0
        String firstUri = buildUri(uriBuilder, 0, page.getSize());
        if (!linkHeader.isEmpty()) linkHeader.append(", ");
        linkHeader.append(buildLinkEntry(firstUri, "first"));

        // Last = última página (totalPages - 1)
        String lastUri = buildUri(uriBuilder, page.getTotalPages() - 1, page.getSize());
        linkHeader.append(", ");
        linkHeader.append(buildLinkEntry(lastUri, "last"));

        return linkHeader.toString();
    }

    /**
     * Construye la URI reemplazando los parámetros page y size.
     *
     * replaceQueryParam: si ya existe "page" en la URL, lo sustituye.
     * Esto evita URLs duplicadas como ?page=0&page=1
     */
    private String buildUri(UriComponentsBuilder uriBuilder, int page, int size) {
        return uriBuilder
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .build()
                .encode()
                .toUriString();
    }

    /**
     * Formatea un link individual según RFC 8288: <url>; rel="relación"
     */
    private String buildLinkEntry(String uri, String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }
}