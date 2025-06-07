package RestInn.dto.habitacionesDTO;

import RestInn.entities.Imagen;
import RestInn.entities.enums.H_Estado;
import RestInn.entities.enums.H_Tipo;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitacionResponseDTO {
    private Long id;
    private Boolean activo;     // Borrado Lógico.
    private H_Estado estado;
    private H_Tipo tipo;
    private Integer numero, piso, capacidad, cantCamas;
    private BigDecimal precioNoche;
    private String comentario;
    private List<Imagen> imagenes;
}
