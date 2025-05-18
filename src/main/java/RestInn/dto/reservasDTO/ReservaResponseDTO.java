package RestInn.dto.reservasDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ReservaResponseDTO {
    private Long id;
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
}
