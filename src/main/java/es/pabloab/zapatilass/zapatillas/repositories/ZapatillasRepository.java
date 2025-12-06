package es.pabloab.zapatilass.zapatillas.repositories;

import es.pabloab.zapatilass.zapatillas.models.Zapatilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ZapatillasRepository extends JpaRepository<Zapatilla, Long> {
    
    @Query("SELECT z FROM Zapatilla z WHERE LOWER(z.marca) LIKE LOWER(CONCAT('%', :marca, '%'))")
    List<Zapatilla> findAllByMarca(@Param("marca") String marca);
    
    @Query("SELECT z FROM Zapatilla z WHERE LOWER(z.tipo) LIKE LOWER(CONCAT('%', :tipo, '%'))")
    List<Zapatilla> findAllByTipo(@Param("tipo") String tipo);
    
    @Query("SELECT z FROM Zapatilla z WHERE LOWER(z.marca) LIKE LOWER(CONCAT('%', :marca, '%')) AND LOWER(z.tipo) LIKE LOWER(CONCAT('%', :tipo, '%'))")
    List<Zapatilla> findAllByMarcaAndTipo(@Param("marca") String marca, @Param("tipo") String tipo);

    Optional<Zapatilla> findByUuid(UUID uuid);

    boolean existsByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}

