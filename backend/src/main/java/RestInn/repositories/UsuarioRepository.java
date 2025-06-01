package RestInn.repositories;

import RestInn.entities.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //hacer buscar usuario por DNI, email, etc.
    Optional<Usuario> findByNombreLogin(String nombreLogin);
    Usuario findByDni(String dni);
    Usuario findByEmail(String email);

}
