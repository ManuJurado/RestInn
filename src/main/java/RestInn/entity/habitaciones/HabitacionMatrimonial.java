package RestInn.entity.habitaciones;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionMatrimonial extends Habitacion {
    private boolean jacuzzi;
}
