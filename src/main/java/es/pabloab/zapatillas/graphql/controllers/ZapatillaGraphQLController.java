package es.pabloab.zapatillas.graphql.controllers;

import es.pabloab.zapatillas.rest.zapatillas.models.Zapatilla;
import es.pabloab.zapatillas.rest.zapatillas.repositories.ZapatillasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class ZapatillaGraphQLController {
    private final ZapatillasRepository zapatillasRepository;

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
}
