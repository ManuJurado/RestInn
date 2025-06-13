package RestInn.entities.usuarios;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Column(unique = true)
    private String nombreLogin;
    private String dni;
    private String phoneNumber;
    @Column(unique = true)
    private String email;
    private String password;
    private String cuit;


    @JsonProperty("role")
    @Transient
    public String getRole() {
        if (this instanceof Cliente) return "CLIENTE";
        else if (this instanceof Empleado) return "EMPLEADO";
        else if (this instanceof Administrador) return "ADMINISTRADOR";
        else return "desconocido";
    }

}
