package RestInn.entity.habitaciones;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionDoble extends Habitacion {
    private boolean camasSeparadas; // Ejemplo
}
