package RestInn.entities.cobranzas;

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
    private Long id;
    private LocalDate fechaEmision;
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

}
