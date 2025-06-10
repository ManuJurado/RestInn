package RestInn.dto.cobranzasDTO;

import RestInn.entities.cobranzas.Consumo;
import RestInn.entities.enums.EstadoFactura;
import RestInn.entities.enums.MetodoPago;
import RestInn.entities.enums.TipoFactura;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class FacturaResponseDTO {
    private Long id;
    private String clienteNombre;

    private LocalDate   ingreso;
    private LocalDate   salida;
    private String      habitacionNumero;


    private Long reservaId;
    private LocalDate fechaEmision;
    private TipoFactura tipoFactura;
    private EstadoFactura estado;
    private List<ConsumoResponseDTO> consumos;
    private BigDecimal subtotal;
    private MetodoPago metodoPago;
    private Integer cuotas;
    private BigDecimal descuento;
    private BigDecimal interes;
    private BigDecimal totalFinal;
}
