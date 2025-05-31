package RestInn.dto.reservasDTO;

import RestInn.entities.enums.EstadoReserva;
import RestInn.validation.ReservaValida;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ReservaValida
public class ReservaRequestDTO {
    @NotNull(message = "La fecha de ingreso es obligatoria.")
    private LocalDate fechaIngreso;

    @NotNull(message = "La fecha de salida es obligatoria.")
    private LocalDate fechaSalida;

    private LocalDate fechaReserva; // opcional: puede ser asignada automáticamente en el service si viene null

    private EstadoReserva estadoReserva = EstadoReserva.PENDIENTE; // valor por defecto, si no se envía

    @NotNull(message = "El ID de la habitación es obligatorio.")
    private Long habitacionId;

    @NotEmpty(message = "Debe especificar al menos un huésped.")
    @Valid
    private List<HuespedRequestDTO> huespedes;
}