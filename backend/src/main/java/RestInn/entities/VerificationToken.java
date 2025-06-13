package RestInn.entities;

import RestInn.entities.enums.TokenType;
import RestInn.entities.usuarios.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    @Id @GeneratedValue
    private Long id;

    private String code;
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private TokenType type;               // ← REGISTRATION o PASSWORD_RESET

    @OneToOne
    @JoinColumn(name="user_id", nullable = true)
    private Usuario usuario;             // Solo se llena en PASSWORD_RESET

    @Lob
    private String userDtoJson;           // Solo en REGISTRATION
}

