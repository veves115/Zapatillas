package es.pabloab.zapatillas.graphql.controllers;

import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.rest.zapatillas.models.Zapatilla;
import es.pabloab.zapatillas.rest.zapatillas.repositories.ZapatillasRepository;
import es.pabloab.zapatillas.rest.zapatillas.services.ZapatillasService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ZapatillaGraphQLController {
    private final ZapatillasRepository zapatillasRepository;
    private final ZapatillasService zapatillasService;

    // -- QUERIES (usan repositorio directamente, como el proyecto de referencia) --

    @QueryMapping
    public List<Zapatilla> zapatillas() {
        return zapatillasRepository.findAll();
    }

    @QueryMapping
    public Zapatilla zapatillaById(@Argument Long id) {
        return zapatillasRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Zapatilla> zapatillasByMarca(@Argument String marca) {
        return zapatillasRepository.findAllByMarcaContainingIgnoreCase(marca);
    }

    @QueryMapping
    public List<Zapatilla> zapatillasByTipo(@Argument String tipo) {
        return zapatillasRepository.findAllByTipoContainingIgnoreCase(tipo);
    }

    // -- MUTATIONS (usan el service para validaciones + notificaciones WebSocket) --

    @MutationMapping
    public ZapatillaResponseDto createZapatilla(@Argument CreateZapatillaInput input) {
        ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                .marca(input.marca())
                .modelo(input.modelo())
                .codigoProducto(input.codigoProducto())
                .talla(input.talla())
                .color(input.color())
                .tipo(input.tipo())
                .precio(input.precio())
                .stock(input.stock())
                .build();

        return zapatillasService.save(dto);
    }

    @MutationMapping
    public ZapatillaResponseDto updateZapatilla(@Argument Long id, @Argument UpdateZapatillaInput input) {
        ZapatillaUpdateDto dto = ZapatillaUpdateDto.builder()
                .codigoProducto(input.codigoProducto())
                .talla(input.talla())
                .color(input.color())
                .tipo(input.tipo())
                .precio(input.precio())
                .stock(input.stock())
                .build();

        return zapatillasService.update(id, dto);
    }

    @MutationMapping
    public Boolean deleteZapatilla(@Argument Long id) {
        zapatillasService.deleteById(id);
        return true;
    }

    // -- Records de input para GraphQL --

    public record CreateZapatillaInput(
            String marca,
            String modelo,
            String codigoProducto,
            Double talla,
            String color,
            String tipo,
            Double precio,
            Integer stock
    ) {}

    public record UpdateZapatillaInput(
            String codigoProducto,
            Double talla,
            String color,
            String tipo,
            Double precio,
            Integer stock
    ) {}
}
