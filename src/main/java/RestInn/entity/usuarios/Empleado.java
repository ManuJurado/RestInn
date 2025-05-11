package RestInn.entity.usuarios;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter @Setter
public class Empleado extends Usuario {

    private String turno;
    private Date fechaIngreso;

    @Enumerated(EnumType.STRING)
    private TipoEmpleado tipoEmpleado;

    private String telefono;
    private String estadoTrabajo;

}
