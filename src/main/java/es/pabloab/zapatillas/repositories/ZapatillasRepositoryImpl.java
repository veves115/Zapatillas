package es.pabloab.zapatillas.repositories;

import es.pabloab.zapatillas.models.Zapatilla;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class ZapatillasRepositoryImpl implements ZapatillasRepository {
    private final Map<Long, Zapatilla> zapatillas = new LinkedHashMap<>(
            Map.of(
                    1L,Zapatilla.builder()
                            .id(1L)
                            .marca("Nike")
                            .modelo("Air Max 90")
                            .codigoProducto("NI1234KE")
                            .talla(42.0)
                            .color("Blanco/Rojo")
                            .tipo("Running")
                            .precio(129.99)
                            .stock(50)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .uuid(UUID.randomUUID())
                            .build(),
                    2L, Zapatilla.builder()
                            .id(2L)
                            .marca("Adidas")
                            .modelo("Samba")
                            .codigoProducto("AD5678AS")
                            .talla(41.5)
                            .color("Negro/Blanco")
                            .tipo("Running")
                            .precio(180.00)
                            .stock(30)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .uuid(UUID.randomUUID())
                            .build()
            )
    );


    @Override
    public List<Zapatilla> findAll() {
        log.info("Buscando todas las zapatillas");
        return zapatillas.values().stream().toList();
    }

    @Override
    public List<Zapatilla> findAllByMarca(String marca) {
        log.info("Buscando todas las zapatillas por marca");
        return zapatillas.values().stream().filter(z -> z.getMarca().toLowerCase().contains(marca.toLowerCase())).toList();
    }

    @Override
    public List<Zapatilla> findAllByTipo(String tipo) {
        log.info("Buscando todas las zapatillas por tipo");
        return zapatillas.values().stream().filter(z -> z.getTipo().toLowerCase().contains(tipo.toLowerCase())).toList();
    }

    @Override
    public List<Zapatilla> findAllByMarcaAndTipo(String marca, String tipo) {
        log.info("Buscando todas las zapatillas por marca: {} y tipo:{}", marca, tipo);
        return zapatillas.values().stream().filter(z -> z.getMarca().toLowerCase().contains(marca.toLowerCase())).toList();
    }

    @Override
    public Optional<Zapatilla> findById(long id) {
        log.info("Buscando todas las zapatillas por id: {}",id);
        return Optional.ofNullable(zapatillas.get(id));
    }

    @Override
    public Optional<Zapatilla> findByUuid(UUID uuid) {
        log.info("Buscando zapatillas por uuid: {}",uuid);
        return zapatillas.values().stream().filter(z -> z.getUuid().equals(uuid)).findFirst();
    }

    @Override
    public boolean existsById(long id) {
        log.info("Comprobando si existe la zapatilla por id: {}",id);
        return zapatillas.containsKey(id);
    }

    @Override
    public boolean existsByUuid(UUID uuid) {
        log.info("Comprobando si existe la zapatilla por uuid: {}",uuid);
        return zapatillas.containsKey(uuid);
    }

    @Override
    public Zapatilla save(Zapatilla zapatilla) {
        log.info("Guardando la zapatilla: {}",zapatilla);
        zapatillas.put(zapatilla.getId(), zapatilla);
        return zapatilla;
    }

    @Override
    public void deleteById(long id) {
        log.info("Eliminando la zapatilla por id: {}",id);
        zapatillas.remove(id);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
    log.info("Eliminando la zapatilla por uuid: {}",uuid);
    zapatillas.values().removeIf(z -> z.getUuid().equals(uuid));
    }

    @Override
    public Long nextId() {
        log.debug("Obteniendo siguiente id de zapatilla");
        return zapatillas.keySet().stream().mapToLong(Long::longValue).max().orElse(0L) + 1;
    }
}
