package RestInn.entities.usuarios;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@DiscriminatorValue("Administrador")
public class Administrador extends Usuario{

}
