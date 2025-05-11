package RestInn.repository.usuariosRepository;

import RestInn.entity.usuarios.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    // Aquí puedes agregar consultas específicas para Empleado si las necesitas
}
