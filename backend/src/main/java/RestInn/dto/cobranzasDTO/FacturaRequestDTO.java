package RestInn.dto.cobranzasDTO;

import RestInn.entities.enums.MetodoPago;
import RestInn.validation.FacturaValida;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FacturaValida
public class FacturaRequestDTO {
    private Long id;    // No obligatorio si es alta

    @NotNull(message = "El ID de la reserva es obligatorio.")
    private Long reservaId;

    private Long clienteId;

    @NotNull(message = "El método de pago es obligatorio.")
    private MetodoPago metodoPago;

    @NotNull(message = "La cantidad de cuotas es obligatoria.")
    private Integer cuotas;
}
