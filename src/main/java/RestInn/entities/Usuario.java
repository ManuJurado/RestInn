package RestInn.entities;

import RestInn.entities.enums.RolEmpleado;
import RestInn.entities.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Optional;

@Entity
@Getter
@Setter
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    @Enumerated(EnumType.STRING)
    private Optional<RolEmpleado> rolEmpleado;

    // Constructor con validación para rolEmpleado solo si el tipo es EMPLEADO
    public Usuario(String nombre, String apellido, String dni, String email, String password, TipoUsuario tipoUsuario, RolEmpleado rolEmpleado) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.tipoUsuario = tipoUsuario;
        if (tipoUsuario == TipoUsuario.EMPLEADO) {
            this.rolEmpleado = Optional.of(rolEmpleado);
        } else {
            this.rolEmpleado = Optional.empty();
        }
    }
}
