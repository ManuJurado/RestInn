package RestInn.dto.cobranzasDTO;

import RestInn.entities.cobranzas.Consumo;
import RestInn.entities.enums.MetodoPago;
import RestInn.entities.enums.TipoFactura;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FacturaResponseDTO {
    private Long id;
    private LocalDate fechaEmision;
    private TipoFactura tipoFactura;
    private List<Consumo> consumos;
    private BigDecimal subtotal;
    private MetodoPago metodoPago;
    private Integer cuotas;
    private BigDecimal descuento;
    private BigDecimal interes;
    private BigDecimal totalFinal;
}
