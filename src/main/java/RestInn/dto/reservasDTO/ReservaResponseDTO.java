package RestInn.dto.reservasDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReservaResponseDTO {

    private Long id;
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
    private LocalDate fechaReserva;

    private Long usuarioId;
    private Long habitacionId;
    private List<Long> huespedesIds;

    private String estado; // Podés usar EstadoReserva directamente si querés devolver el enum
}
