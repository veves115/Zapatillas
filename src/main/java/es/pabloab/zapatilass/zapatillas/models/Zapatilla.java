package es.pabloab.zapatilass.zapatillas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "zapatillas")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zapatilla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marca;
    
    @Column(nullable = false)
    private String modelo;
    
    @Column(unique = true, nullable = false)
    private String codigoProducto;
    
    private Double talla;
    
    @Column(nullable = false)
    private Double precio;
    
    private String color;
    
    private String tipo;
    
    private Integer stock;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;
}
