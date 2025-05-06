package RestInn.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class HabitacionDTO {
    private Long id;
    private String tipo;
    private Integer capacidad;
    private Double precio;
    private Boolean disponibilidad;
}