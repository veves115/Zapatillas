package es.pabloab.zapatilass.zapatillas.repositories;


import es.pabloab.zapatilass.zapatillas.models.Zapatilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ZapatillasRepositoryImplTest {
    private ZapatillasRepository repository;
    @BeforeEach
    void setUp() {
        repository = new ZapatillasRepositoryImpl();
    }
    @Test
    void findAllDevuelveDatosIniciales(){
        List<Zapatilla> zapatillas = repository.findAll();
        assertThat(zapatillas).isNotNull().hasSizeGreaterThanOrEqualTo(2);
    }
    @Test
    void findAllByMarcaIgnoraMayusculas(){
        List<Zapatilla> zapatillas = repository.findAllByMarca("nike");

        assertThat(zapatillas).allSatisfy(zapatilla -> assertThat(zapatilla.getMarca()).containsIgnoringCase("nike"));
    }
    @Test
    void findAllByTipoIgnoraMayusculas(){
        List<Zapatilla> zapatillas = repository.findAllByTipo("running");
        assertThat(zapatillas).allSatisfy(zapatilla -> assertThat(zapatilla.getTipo()).containsIgnoringCase("running"));
    }
    @Test
    void findAllByMarcaAndTipoFiltraCorrectamente(){
        List<Zapatilla> zapatillas = repository.findAllByMarcaAndTipo("nike", "running");
        assertThat(zapatillas).hasSize(1)
                .first()
                .satisfies(zapatilla -> {
                    assertThat(zapatilla.getMarca()).isEqualTo("Nike");
                    assertThat(zapatilla.getTipo()).isEqualTo("Running");
                });
    }
    @Test
    void findByIdDevuelveOptional(){
        assertThat(repository.findById(1L).isPresent()).isTrue();
        assertThat(repository.findById(19999L).isEmpty()).isFalse();
    }
    @Test
    void findByUuidDevuelveElementoCuandoExiste(){
        UUID uuidExiste = repository.findAll().get(0).getUuid();
        Zapatilla zapatilla = repository.findByUuid(uuidExiste);
        assertThat(zapatilla).isNotNull();
        assertThat(zapatilla.getUuid()).isEqualTo(uuidExiste);
    }
    @Test
    void savePersisteNuevosElementos(){
        Long nuevoId = repository.nextId();
        Zapatilla nuevaZapatilla = Zapatilla.builder()
                .id(nuevoId)
                .marca("Puma")
                .modelo("Runner")
                .codigoProducto("PU0001MA")
                .talla(43.0)
                .color("Azul")
                .tipo("Running")
                .precio(99.99)
                .stock(20)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();

        Zapatilla guardada = repository.save(nuevaZapatilla);

        assertThat(guardada).isEqualTo(nuevaZapatilla);
        assertThat(repository.findById(nuevoId)).contains(nuevaZapatilla);
    }
    @Test
    void deleteByUuIdEliminanElementos(){
        Zapatilla zapatilla = repository.findAll().get(0);
        repository.deleteById(zapatilla.getId());

        assertThat(repository.existsById(zapatilla.getId())).isFalse();

        repository.save(zapatilla);
        repository.deleteByUuid(zapatilla.getUuid());

        assertThat(repository.existsByUuid(zapatilla.getUuid())).isFalse();

    }
    @Test
    void nextIdIncrementaSecuencia(){
        Long siguiente = repository.nextId();
        Zapatilla zapatillaBase = repository.findAll().get(0);
        Zapatilla nueva = Zapatilla.builder()
                .id(siguiente)
                .marca(zapatillaBase.getMarca())
                .modelo(zapatillaBase.getModelo())
                .codigoProducto(zapatillaBase.getCodigoProducto())
                .talla(zapatillaBase.getTalla())
                .color(zapatillaBase.getColor())
                .tipo(zapatillaBase.getTipo())
                .precio(zapatillaBase.getPrecio())
                .stock(zapatillaBase.getStock())
                .createdAt(zapatillaBase.getCreatedAt())
                .updatedAt(zapatillaBase.getUpdatedAt())
                .uuid(zapatillaBase.getUuid())
                .build();
        repository.save(nueva);

        assertThat(repository.nextId()).isGreaterThan(siguiente);
    }
}

