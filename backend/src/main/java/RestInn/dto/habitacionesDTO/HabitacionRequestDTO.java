package RestInn.dto.habitacionesDTO;

import RestInn.entities.enums.H_Estado;
import RestInn.entities.enums.H_Tipo;
import RestInn.validation.HabitacionValida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@HabitacionValida
public class HabitacionRequestDTO { //Recibe del front para la creacion o edicion de la entidad

    @NotNull(message = "El estado es obligatorio")
    private H_Estado estado;

    @NotNull(message = "El tipo es obligatorio")
    private H_Tipo tipo;

    @NotNull(message = "El número es obligatorio")
    private Integer numero;

    @NotNull(message = "El piso es obligatorio")
    private Integer piso;

    @NotNull(message = "La capacidad es obligatoria")
    private Integer capacidad;

    @NotNull(message = "La cantidad de camas es obligatoria")
    private Integer cantCamas;

    @NotNull(message = "El precio por noche es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio por noche debe ser mayor a 0")
    private BigDecimal precioNoche;

    private String comentario;

    private Boolean activo;
}
