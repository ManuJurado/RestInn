package RestInn.repositories;

import RestInn.entities.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
}