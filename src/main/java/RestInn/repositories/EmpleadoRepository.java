package RestInn.repositories;

import RestInn.entities.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    public Empleado findByNombre();
    // Aquí puedes agregar consultas específicas para Empleado si las necesitas
}
