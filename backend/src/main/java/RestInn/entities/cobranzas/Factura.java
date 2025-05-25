package RestInn.entities.cobranzas;

import RestInn.entities.Habitacion;
import RestInn.entities.Reserva;
import RestInn.entities.enums.EstadoFactura;
import RestInn.entities.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaEmision;

    @OneToMany(cascade = CascadeType.ALL)  // asumo la relación con consumos, ajustá según necesidad
    private List<Consumo> consumos;

    private BigDecimal subtotal;  // reserva + consumos
    private MetodoPago metodoPago;
    private Double cuotas;
    private Double descuento;   // porcentual
    private Double interes;     // porcentual
    private BigDecimal totalFinal;
    private EstadoFactura estado;
    private Double haber;   // pagado
    private Double debe;    // deuda

    @ManyToOne
    @JoinColumn(name = "habitacion_id")
    private Habitacion habitacion;  // <-- ESTE CAMPO FALTABA
}
