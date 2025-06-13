package RestInn.entities.usuarios;

import jakarta.persistence.GeneratedValue;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PendingRegistration {
    @Id
    @GeneratedValue
    Long id;
    private String code;                // 6 dígitos
    private LocalDateTime expiresAt;
    private String dtoJson;             // guardo el UsuarioRequestDTO como JSON
}
