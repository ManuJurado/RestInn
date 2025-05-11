package RestInn.repository.usuariosRepository;

import RestInn.entity.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //hacer buscar usuario por DNI, email, etc.
    Usuario findByDni(String dni);
    Usuario findByEmail(String email);
}
