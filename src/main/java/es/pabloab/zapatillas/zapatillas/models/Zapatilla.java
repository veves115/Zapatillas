package es.pabloab.zapatillas.zapatillas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que representa una zapatilla
 * <p>
 * ANOTACIONES JPA:
 *
 * @Entity: Marca la clase como una entidad JPA (tabla en BD)
 * @Table: Especifica el nombre de la tabla
 * @Id: Marca el campo como Primary Key
 * @GeneratedValue: Indica cómo se genera el ID automáticamente
 * @Column: Configura la columna en la BD
 */
@Entity  // ← Marca como entidad JPA
@Table(name = "zapatillas")  // ← Nombre de la tabla en la BD
@Data
@Builder
@NoArgsConstructor  // ← JPA lo necesita
@AllArgsConstructor // ← Builder lo necesita
public class Zapatilla {

    // ===== PRIMARY KEY =====

    /**
     * @Id: Marca como Primary Key
     * @GeneratedValue: Genera el valor automáticamente
     * strategy = IDENTITY: Auto-increment en la BD
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== DATOS BÁSICOS =====

    /**
     * @Column: Configura la columna
     * nullable = false: NOT NULL en la BD
     * length = 50: VARCHAR(50)
     */
    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    /**
     * unique = true: UNIQUE constraint
     * Dos zapatillas no pueden tener el mismo código
     */
    @Column(unique = true, nullable = false, length = 10)
    private String codigoProducto;

    @Column(nullable = false)
    private Double talla;

    @Column(nullable = false, length = 50)
    private String color;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer stock;

    // ===== AUDITORÍA =====

    /**
     * @CreationTimestamp: Hibernate asigna automáticamente la fecha de creación
     * updatable = false: No se puede modificar después de crear
     */
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * @UpdateTimestamp: Hibernate actualiza automáticamente la fecha en cada update
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * UUID único para cada zapatilla
     * columnDefinition: Define el tipo exacto en la BD
     */
    @Column(unique = true, nullable = false, updatable = false,
            columnDefinition = "UUID")
    private UUID uuid;

    /**
     * @PrePersist: Se ejecuta ANTES de guardar por primera vez
     * Genera el UUID automáticamente
     */
    @PrePersist
    public void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
}
