package es.pabloab.zapatilass.zapatillas.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "zapatillas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Zapatilla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 50)
    private String marca;
    @Column(nullable = false,length = 50)
    private String modelo;
    @Column(unique = true,nullable = false,length = 10)
    private String codigoProducto;
    @Column(nullable = false)
    private String talla;
    @Column(nullable = false,length = 50)
    private String color;
    @Column(nullable = false,length = 20)
    private String tipo;
    @Column(nullable = false)
    private Integer precio;
    @Column(nullable = false)
    private Integer stock;

    @CreationTimeStamp
    @Column(updatable = false,nullable = false)
    private LocalDateTime updatedAt;
}
