package RestInn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FacturacionDTO {
    private Long id;
    private Double montoTotal;
    private String metodoPago;
    private Long clienteId;
    private Long reservaId;  // Relación con Reserva
}
