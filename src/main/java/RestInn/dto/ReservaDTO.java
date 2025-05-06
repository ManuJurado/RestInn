package RestInn.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ReservaDTO {
    private Long id;
    private String fechaEntrada;
    private String fechaSalida;
    private String estado;  // Ejemplo: "Confirmada", "Pendiente"
    private Double precioTotal;
    private Long clienteId;  // Relación con Cliente
    private Long habitacionId;  // Relación con Habitacion
}