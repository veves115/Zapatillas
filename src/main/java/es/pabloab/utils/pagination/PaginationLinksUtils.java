package es.pabloab.utils.pagination;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
@Component
public class PaginationLinksUtils {
    public String createLinkHeader(Page<?>page,UriComponentsBuilder uriBuilder){
        final StringBuilder linkHeader = new StringBuilder();

        if (page.hasNext()){
            String uri = constructUri(page.getNumber()+1,page.getSize(),uriBuilder);
            linkHeader.append(buildLinkHeader(uri,"next"));
        }
    }
}
