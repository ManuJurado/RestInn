package RestInn.entities.usuarios;

import RestInn.entities.enums.RolEmpleado;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado extends Usuario{
    @Enumerated(EnumType.STRING)
    private RolEmpleado rolEmpleado;

    public Empleado(String nombre, String apellido, String dni, String email, String password, RolEmpleado rolEmpleado) {
        super(nombre, apellido, dni, email, password);
        this.rolEmpleado = rolEmpleado;
    }
}
