package RestInn.repositories;

import RestInn.entities.Habitacion;
import RestInn.entities.Imagen;
import RestInn.entities.enums.H_Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagenRepository extends JpaRepository<Imagen, Long> {

}
