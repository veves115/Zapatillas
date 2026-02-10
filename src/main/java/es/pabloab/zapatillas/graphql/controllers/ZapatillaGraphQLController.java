package es.pabloab.zapatillas.graphql.controllers;

import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.rest.zapatillas.mappers.ZapatillaMapper;
import es.pabloab.zapatillas.rest.zapatillas.models.Zapatilla;
import es.pabloab.zapatillas.rest.zapatillas.repositories.ZapatillasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class ZapatillaGraphQLController {
    private final ZapatillasRepository zapatillasRepository;
    private final ZapatillaMapper mapper;

    // -- QUERIES --
    @QueryMapping
    public List<Zapatilla> zapatillas() {
        //Devuelve todas las zapatillas
        return zapatillasRepository.findAll();
    }
    @QueryMapping
    public Zapatilla zapatillaById(@Argument Long id) {
        //Busca una zapatilla por su ID,devuelve null si no existe
        Optional<Zapatilla> zapatillaoPT = zapatillasRepository.findById(id);
        return zapatillaoPT.orElse(null);
    }
    @QueryMapping
    public List<Zapatilla> zapatillasByMarca(@Argument String marca) {
        //Reutiliza el método del repositorio
        return zapatillasRepository.findAllByMarcaContainingIgnoreCase(marca);
    }
    @QueryMapping
    public List<Zapatilla> zapatillasByTipo(@Argument String tipo) {
        return zapatillasRepository.findAllByTipoContainingIgnoreCase(tipo);
    }

    // --- MUTATIONS ---

    @MutationMapping
    public Zapatilla createZapatilla(@Argument CreateZapatillaInput input) {
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

        Zapatilla nueva = mapper.toZapatilla(null,dto);
        return zapatillasRepository.save(nueva);
    }
    @MutationMapping
    public Zapatilla updateZapatilla(@Argument Long id, @Argument UpdateZapatillaInput input) {
        // Buscamos la zapatilla existente
        Optional<Zapatilla> zapatillaOpt = zapatillasRepository.findById(id);
        if (zapatillaOpt.isEmpty()) {
            return null; // GraphQL devolverá null (el schema lo permite)
        }

        Zapatilla actual = zapatillaOpt.get();

        // Construimos el DTO de update desde el input
        ZapatillaUpdateDto dto = ZapatillaUpdateDto.builder()
                .codigoProducto(input.codigoProducto())
                .talla(input.talla())
                .color(input.color())
                .tipo(input.tipo())
                .precio(input.precio())
                .stock(input.stock())
                .build();

        // Reutilizamos el mapper (respeta los campos null → mantiene el valor actual)
        Zapatilla actualizada = mapper.toZapatilla(dto, actual);
        return zapatillasRepository.save(actualizada);
    }
    @MutationMapping
    public Boolean deleteZapatilla(@Argument Long id) {
        if (!zapatillasRepository.existsById(id)) {
            return false;
        }
        zapatillasRepository.deleteById(id);
        return true;
    }
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
