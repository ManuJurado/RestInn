package RestInn.repositories;

import RestInn.entities.VerificationToken;
import RestInn.entities.enums.TokenType;
import RestInn.entities.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByCodeAndType(String code, TokenType type);

    @Modifying @Transactional
    void deleteByUsuarioAndType(Usuario usuario, TokenType type);
}
