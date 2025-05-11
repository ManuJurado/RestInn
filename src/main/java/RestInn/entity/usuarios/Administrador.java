package RestInn.entity.usuarios;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Administrador extends Usuario {

    // El Administrador no tiene atributos adicionales, solo su id y el hecho de ser un Administrador
}
