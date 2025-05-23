package RestInn.repositories;

import RestInn.entities.Habitacion;
import RestInn.entities.enums.H_Estado;
import RestInn.entities.enums.H_Tipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    // Buscar habitación por número
    Optional<Habitacion> findByNumero(Integer numero);

    // Buscar todas las habitaciones activas con cierto estado
    List<Habitacion> findByActivoTrueAndEstado(H_Estado estado);

    // Buscar todas las habitaciones activas con cierta cantidad de camas
    List<Habitacion> findByActivoTrueAndCantCamas(Integer cantCamas);

    // Buscar todas las habitaciones activas con cierta capacidad
    List<Habitacion> findByActivoTrueAndCapacidad(Integer capacidad);

    // Buscar todas las habitaciones activas con cierto tipo
    List<Habitacion> findByActivoTrueAndTipo(H_Tipo tipo);

    // Buscar habitaciones activas por número y tipo
    List<Habitacion> findByActivoTrueAndNumeroAndTipo(Integer numero, H_Tipo tipo);
}
