package RestInn.dto.reservasDTO;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import RestInn.validation.FechasValidas;

@Data
@FechasValidas
public class ReservaRequestDTO {

    @NotNull(message = "fecha ingreso obligatoria")
    private LocalDate fechaIngreso;
    @NotNull(message = "fecha egreso obligatoria")
    private LocalDate fechaSalida;
}