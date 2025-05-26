package RestInn.dto.cobranzasDTO;

import RestInn.entities.cobranzas.Consumo;
import RestInn.entities.enums.EstadoFactura;
import RestInn.entities.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FacturaResponseDTO {
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
