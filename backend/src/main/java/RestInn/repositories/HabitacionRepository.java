package RestInn.repositories;

import RestInn.entities.Habitacion;
import RestInn.entities.enums.H_Estado;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long>, JpaSpecificationExecutor<Habitacion> {

    // Encontrar todas las habitaciones que están activas.
    List<Habitacion> findByActivoTrue();

    // Encontrar todas, sin discriminar activo/inactivo. /
    List<Habitacion> findAll();

    List<Habitacion> findByEstadoNot(H_Estado estado);

    // Necesario para bloquear el acceso de reservas por parte de la creacion de una reserva.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM Habitacion h WHERE h.id = :id")
    Optional<Habitacion> findByIdConBloqueo(@Param("id") Long id);
}
