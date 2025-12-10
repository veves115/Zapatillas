package es.pabloab.utils.pagination;

import java.util.List;

public record PageResponse<Z>(
        List<Z> content,
        int totalPages,
        long totalElements,
        int pageSize,
        int pageNumber,
        int totalPageElements,
        boolean empty,
        boolean first,
        boolean last,
        String sortBy,
        String direction
) {
    public static <Z> PageResponse<Z> of(Page<Z>page, String sortBy, String direction)

}
