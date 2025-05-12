package RestInn.dto.reservasDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ReservaRequestDTO {

    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
}