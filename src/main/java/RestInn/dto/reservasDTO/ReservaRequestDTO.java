package RestInn.dto.reservasDTO;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import RestInn.validation.FechaValidaValidator;
import RestInn.validation.FechasValidas;


@Data
@FechasValidas
public class ReservaRequestDTO {

    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
}