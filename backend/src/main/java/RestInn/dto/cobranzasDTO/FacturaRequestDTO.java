package RestInn.dto.cobranzasDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FacturaRequestDTO {
    private Long id;
    private Double montoTotal;
    private String metodoPago;
    private Long clienteId;
    private Long reservaId;  // Relación con Reserva
}
