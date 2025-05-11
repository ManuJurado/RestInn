package RestInn.repository.usuariosRepository;

import RestInn.entity.usuarios.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    // Aquí puedes agregar consultas específicas para Administrador si las necesitas
}
