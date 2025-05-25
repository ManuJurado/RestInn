package RestInn.dto.habitacionesDTO;

import RestInn.entities.Imagen;
import RestInn.entities.enums.H_Estado;
import RestInn.entities.enums.H_Tipo;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitacionResponseDTO {
    private Long id;
    private Boolean activo;     // Borrado Lógico.
    private Boolean disponible; // Listo para usar.
    private H_Estado estado;
    private H_Tipo tipo;
    private Integer numero, capacidad, cantCamas;
    private BigDecimal precioNoche;
    private String comentario;
    private Imagen imagen;
}