package RestInn.entity.habitaciones;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionSuite extends Habitacion {
    private boolean balcon;
    private boolean cocina;
}

