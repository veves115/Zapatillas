package es.pabloab.zapatillas.zapatillas.repositories;


import es.pabloab.zapatillas.zapatillas.models.Zapatilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA para Zapatillas
 * <p>
 * HEREDA DE JpaRepository<Entidad, TipoID>
 * <p>
 * JpaRepository proporciona automáticamente:
 * - save(entity)
 * - findById(id)
 * - findAll()
 * - deleteById(id)
 * - existsById(id)
 * - count()
 * - Y muchos más...
 * <p>
 * MÉTODOS PERSONALIZADOS:
 * Spring Data JPA genera las queries automáticamente
 * basándose en el NOMBRE del método.
 */
@Repository
public interface ZapatillasRepository extends JpaRepository<Zapatilla, Long> {

    // ===== MÉTODOS GENERADOS AUTOMÁTICAMENTE POR SPRING DATA JPA =====

    /**
     * Busca por marca (query derivada del nombre)
     * <p>
     * Nombre del método: findAllByMarcaContainingIgnoreCase
     * - findAll: Devuelve lista
     * - ByMarca: Filtro por campo "marca"
     * - Containing: SQL LIKE '%valor%'
     * - IgnoreCase: Case insensitive
     * <p>
     * SQL generado:
     * SELECT * FROM zapatillas WHERE LOWER(marca) LIKE LOWER('%?%')
     */
    List<Zapatilla> findAllByMarcaContainingIgnoreCase(String marca);

    /**
     * Busca por tipo (case insensitive)
     */
    List<Zapatilla> findAllByTipoContainingIgnoreCase(String tipo);

    /**
     * Busca por marca Y tipo
     * <p>
     * SQL generado:
     * SELECT * FROM zapatillas
     * WHERE LOWER(marca) LIKE LOWER('%?%')
     * AND LOWER(tipo) LIKE LOWER('%?%')
     */
    List<Zapatilla> findAllByMarcaContainingIgnoreCaseAndTipoContainingIgnoreCase(
            String marca,
            String tipo
    );

    /**
     * Busca por UUID
     */
    Optional<Zapatilla> findByUuid(UUID uuid);

    /**
     * Verifica si existe por UUID
     */
    boolean existsByUuid(UUID uuid);

    /**
     * Elimina por UUID
     */
    void deleteByUuid(UUID uuid);

    // ===== QUERIES PERSONALIZADAS CON @Query =====

    /**
     * Busca por rango de precio
     *
     * @Query: Define la query manualmente (JPQL)
     * JPQL: Java Persistence Query Language (usa nombres de clases/campos Java)
     * :minPrecio y :maxPrecio son parámetros nombrados
     */
    @Query("SELECT z FROM Zapatilla z WHERE z.precio BETWEEN :minPrecio AND :maxPrecio")
    List<Zapatilla> findByPrecioRange(
            @Param("minPrecio") Double minPrecio,
            @Param("maxPrecio") Double maxPrecio
    );

    /**
     * Busca zapatillas con stock bajo
     */
    @Query("SELECT z FROM Zapatilla z WHERE z.stock < :umbral ORDER BY z.stock ASC")
    List<Zapatilla> findByStockBajo(@Param("umbral") Integer umbral);

    /**
     * Cuenta zapatillas por tipo
     */
    @Query("SELECT COUNT(z) FROM Zapatilla z WHERE z.tipo = :tipo")
    Long countByTipo(@Param("tipo") String tipo);

    /**
     * Busca por código de producto (método simple)
     */
    Optional<Zapatilla> findByCodigoProducto(String codigoProducto);

    // ===== EJEMPLOS DE NOMENCLATURA DE MÉTODOS =====

    // Busca por campo exacto
    // List<Zapatilla> findByMarca(String marca);

    // Busca por campo que empieza con
    // List<Zapatilla> findByMarcaStartingWith(String prefix);

    // Busca por campo que termina con
    // List<Zapatilla> findByMarcaEndingWith(String suffix);

    // Busca por campo mayor que
    // List<Zapatilla> findByPrecioGreaterThan(Double precio);

    // Busca por campo menor que
    // List<Zapatilla> findByPrecioLessThan(Double precio);

    // Busca por campo entre valores
    // List<Zapatilla> findByPrecioBetween(Double min, Double max);

    // Busca ordenado
    // List<Zapatilla> findAllByOrderByPrecioAsc();
    // List<Zapatilla> findAllByOrderByPrecioDesc();

    // Busca con paginación (se explicará más adelante)
    // Page<Zapatilla> findAll(Pageable pageable);
}
