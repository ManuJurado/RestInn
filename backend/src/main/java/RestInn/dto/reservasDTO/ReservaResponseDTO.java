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
        private String estado;
        private Integer habitacionNumero;
        private List<HuespedResponseDTO> huespedes;
}
