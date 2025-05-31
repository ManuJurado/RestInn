package RestInn.dto.cobranzasDTO;

import RestInn.entities.cobranzas.Consumo;
import RestInn.entities.enums.EstadoFactura;
import RestInn.entities.enums.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaRequestDTO {
    @NotNull(message = "El ID de la factura es obligatorio.")
    private Long id;

    @NotNull(message = "El método de pago es obligatorio.")
    private MetodoPago metodoPago;

    @NotNull(message = "La cantidad de cuotas es obligatoria.")
    private Integer cuotas;

    private BigDecimal descuento = BigDecimal.valueOf(0.00); // valor por defecto

    private BigDecimal interes = BigDecimal.valueOf(0.00);   // valor por defecto

    private EstadoFactura estado = EstadoFactura.PENDIENTE;  // valor por defecto
}
