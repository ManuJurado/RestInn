package RestInn.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ReservaDTO {
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida; // Relación con Habitacion
}