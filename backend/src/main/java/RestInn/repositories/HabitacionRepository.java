package RestInn.repositories;

import RestInn.entities.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long>, JpaSpecificationExecutor<Habitacion> {

}
