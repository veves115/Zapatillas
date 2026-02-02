package es.pabloab.zapatillas.rest.zapatillas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity  // ← Marca como entidad JPA
@Table(name = "zapatillas")  // ← Nombre de la tabla en la BD
@Data
@Builder
@NoArgsConstructor  // ← JPA lo necesita
@AllArgsConstructor // ← Builder lo necesita
public class Zapatilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String marca;
    @Column(nullable = false, length = 100)
    private String modelo;
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
    @CreationTimestamp
    @Column(updatable = false, nullable = false, name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(unique = true, nullable = false, updatable = false,
            columnDefinition = "UUID")
    private UUID uuid;
    @PrePersist
    public void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
}
