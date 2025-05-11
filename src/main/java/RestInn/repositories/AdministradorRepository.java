package RestInn.repositories;

import RestInn.entities.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    // Aquí puedes agregar consultas específicas para Administrador si las necesitas
}
