package RestInn.repositories;

import RestInn.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Puedes agregar consultas personalizadas si es necesario
    List<Cliente> findByTipoCliente(String tipoCliente);
    List<Cliente> findByNombreAndApellido(String nombre, String apellido);
}