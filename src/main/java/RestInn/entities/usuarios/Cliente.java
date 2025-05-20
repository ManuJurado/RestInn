package RestInn.entities.usuarios;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente extends Usuario{

    public Cliente(String nombre, String apellido, String dni, String email, String password) {
        super(nombre, apellido, dni, email, password);
    }
}
