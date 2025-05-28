package RestInn.repositories;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.entities.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long>, JpaSpecificationExecutor<Habitacion> {

}
