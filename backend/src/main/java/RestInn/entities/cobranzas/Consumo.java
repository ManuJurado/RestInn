package RestInn.entities.cobranzas;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "consumos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consumo_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @NotBlank
    @Column(nullable = false)
    private String descripcion;

    @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    @DecimalMin("0.00")
    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    @DecimalMin("0.00")
    @Column(nullable = false)
    private BigDecimal subtotal;
}
