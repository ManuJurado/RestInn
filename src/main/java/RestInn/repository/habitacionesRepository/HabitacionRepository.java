package RestInn.repository.habitacionesRepository;

import RestInn.entity.habitaciones.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
}