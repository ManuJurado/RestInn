package RestInn.repository;

import RestInn.entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
}