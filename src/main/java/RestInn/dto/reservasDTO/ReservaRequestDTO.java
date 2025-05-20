package RestInn.dto.reservasDTO;

import RestInn.validation.ReservaValida;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ReservaValida // validador personalizado que asegura que fechaIngreso < fechaSalida
public class ReservaRequestDTO {

    @NotNull(message = "La fecha de ingreso es obligatoria.")
    private LocalDate fechaIngreso;

    @NotNull(message = "La fecha de salida es obligatoria.")
    private LocalDate fechaSalida;

    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long usuarioId;

    @NotNull(message = "El ID de la habitación es obligatorio.")
    private Long habitacionId;

    private List<HuespedRequestDTO> huespedes;
}
