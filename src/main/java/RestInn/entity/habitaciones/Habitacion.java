package RestInn.entity.habitaciones;

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
public abstract class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipo;
    private Integer numero;
    private Integer capacidad;
    private List<String> camas;
    private Boolean disponible;
    private EstadoHabitacion estado;  //(alquilada, limpieza, reparación, desinfección, etc. Detallar el motivo)
    private String detalleEstado;
    private Double precioNoche;
}
