package RestInn.dto;

import RestInn.entities.enums.H_Estado;
import RestInn.entities.enums.H_Tipo;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class HabitacionDTO {
    private Boolean activo;     // Borrado Lógico.
    private Boolean disponible; // Listo para usar.
    private H_Estado estado;
    private H_Tipo tipo;
    private Integer numero, capacidad, cantCamas;
    private Double precioNoche;
}