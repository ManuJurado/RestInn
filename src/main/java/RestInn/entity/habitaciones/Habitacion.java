package RestInn.entity.habitaciones;

import RestInn.entity.habitaciones.enums.H_Estado;
import RestInn.entity.habitaciones.enums.H_Tipo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer numero, capacidad;
    private Boolean activo;     // Borrado Lógico.
    private Boolean disponible; // Listo para usar.
    private H_Estado estado;
    private H_Tipo tipo;
    private Double precioNoche;
}
