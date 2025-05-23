package RestInn.repositories;

import RestInn.entities.usuarios.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol,Integer> {
    Optional<Rol> findByName(String name);
}
