package es.pabloab.zapatillas.zapatillas.repositories;


import es.pabloab.zapatillas.zapatillas.models.Zapatilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ZapatillasRepository extends JpaRepository<Zapatilla, Long> {
    List<Zapatilla> findAllByMarcaContainingIgnoreCase(String marca);
    List<Zapatilla> findAllByTipoContainingIgnoreCase(String tipo);
    List<Zapatilla> findAllByMarcaContainingIgnoreCaseAndTipoContainingIgnoreCase(
            String marca,
            String tipo
    );

    Optional<Zapatilla> findByUuid(UUID uuid);

    boolean existsByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    @Query("SELECT z FROM Zapatilla z WHERE z.precio BETWEEN :minPrecio AND :maxPrecio")
    List<Zapatilla> findByPrecioRange(
            @Param("minPrecio") Double minPrecio,
            @Param("maxPrecio") Double maxPrecio
    );

    @Query("SELECT z FROM Zapatilla z WHERE z.stock < :umbral ORDER BY z.stock ASC")
    List<Zapatilla> findByStockBajo(@Param("umbral") Integer umbral);

    @Query("SELECT COUNT(z) FROM Zapatilla z WHERE z.tipo = :tipo")
    Long countByTipo(@Param("tipo") String tipo);

    Optional<Zapatilla> findByCodigoProducto(String codigoProducto);

}
