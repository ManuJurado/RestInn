package RestInn.dto.habitacionesDTO;

import RestInn.entities.Imagen;
import RestInn.entities.enums.H_Estado;
import RestInn.entities.enums.H_Tipo;

public class HabitacionResponseDTO {
    private Long id;
    private Boolean activo;     // Borrado Lógico.
    private Boolean disponible; // Listo para usar.
    private H_Estado estado;
    private H_Tipo tipo;
    private Integer numero, capacidad, cantCamas;
    private Double precioNoche;
    private String comentario;
    private Imagen imagen;
}