package RestInn.entities.cobranzas;

import RestInn.entities.Reserva;
import RestInn.entities.enums.MetodoPago;
import RestInn.entities.enums.TipoFactura;
import RestInn.entities.usuarios.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoFactura tipoFactura;

    @OneToMany(mappedBy = "factura", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Consumo> consumos;

    @Column(nullable = false)
    @DecimalMin("0.00")
    private BigDecimal subtotal;  // reserva habitacion

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    @Column(nullable = false)
    private Integer cuotas;

    @DecimalMin("0.00")
    private BigDecimal descuento;   // porcentual

    @DecimalMin("0.00")
    private BigDecimal interes;     // porcentual

    @Column(name = "total_final", nullable = false)
    @DecimalMin("0.00")
    private BigDecimal totalFinal;
}