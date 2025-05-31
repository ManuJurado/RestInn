package RestInn.entities.usuarios;

import RestInn.entities.enums.RolEmpleado;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true) // IMPORTANTE: constructor sin args para JPA y evitar error de constructor no definido
@Getter
@Setter
@DiscriminatorValue("Empleado")
public class Empleado extends Usuario{
    @Enumerated(EnumType.STRING)
    private RolEmpleado rolEmpleado;
}
