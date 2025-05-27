package RestInn.entities.usuarios;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean activo;
    private String nombre;
    private String apellido;
    private String nombreLogin;
    private String dni;
    private String phoneNumber;
    private String email;
    private String password;
    private String CUIT;

}
