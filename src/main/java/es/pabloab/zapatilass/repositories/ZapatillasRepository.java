package es.pabloab.zapatilass.repositories;

import es.pabloab.zapatilass.models.Zapatilla;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ZapatillasRepository {
    List<Zapatilla> findAll();
    List<Zapatilla> findAllByMarca(String marca);
    List<Zapatilla> findAllByTipo(String tipo);
    List<Zapatilla> findAllByMarcaAndTipo(String marca, String tipo);

    Optional<Zapatilla> findById(long id);
    Zapatilla findByUuid(UUID uuid);

    boolean existsById(long id);
    boolean existsByUuid(UUID uuid);

    Zapatilla save(Zapatilla zapatilla);

    void deleteById(long id);
    void deleteByUuid(UUID uuid);

    Long nextId();
}

