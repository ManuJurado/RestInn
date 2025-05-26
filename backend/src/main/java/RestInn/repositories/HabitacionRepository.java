package RestInn.repositories;

import RestInn.entities.Habitacion;
import RestInn.entities.enums.H_Estado;
import RestInn.entities.enums.H_Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long>, JpaSpecificationExecutor<Habitacion> {
    // Buscar habitación por ID
    Optional<Habitacion> findById(Integer id);
}
