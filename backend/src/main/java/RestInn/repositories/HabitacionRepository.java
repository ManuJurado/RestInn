package RestInn.repositories;

import RestInn.entities.Habitacion;
import RestInn.entities.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long>, JpaSpecificationExecutor<Habitacion> {
    // Buscar habitación por ID
    Optional<Habitacion> findById(Integer id);

    Imagen saveImagen(Imagen imagen);
    Optional<Imagen> findByImagenId(Long id);
}
