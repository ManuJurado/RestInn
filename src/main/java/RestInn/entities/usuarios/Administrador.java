package RestInn.entities.usuarios;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Administrador extends Usuario{

    public Administrador(String nombre, String apellido, String dni, String email, String password) {
        super(nombre, apellido, dni, email, password);
    }
}
