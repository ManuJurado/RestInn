package RestInn.repositories;

import RestInn.entities.VerificationToken;
import RestInn.entities.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    Optional<VerificationToken> findByCode(String code);

    @Modifying
    @Transactional
    void deleteByUsuario(Usuario usuario);

}

