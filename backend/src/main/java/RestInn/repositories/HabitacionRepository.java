package RestInn.repositories;

import RestInn.entities.Habitacion;
import RestInn.entities.enums.H_Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long>, JpaSpecificationExecutor<Habitacion> {

    List<Habitacion> findByEstadoNot(H_Estado estado);
}
