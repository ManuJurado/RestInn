package RestInn.entity.usuarios;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Inheritance(strategy = InheritanceType.JOINED) // Especifica la estrategia de herencia
@Entity
@Getter @Setter // Lombok generará los getters y setters automáticamente
public abstract class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Se asume que 'id' es un campo autoincremental
    private Long id;

    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String password;
}
