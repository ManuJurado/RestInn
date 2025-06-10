package RestInn.entities;

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
    @Id
    @GeneratedValue
    private Long id;

    private String code;
    private LocalDateTime expiresAt;

    @OneToOne
    @JoinColumn(name="user_id")
    private Usuario usuario;

}
