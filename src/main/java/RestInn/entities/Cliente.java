package RestInn.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
public class Cliente extends Usuario {

    private String direccion;
    private String telefono;
    private int puntosFidelidad;
    private String tipoCliente;
    private Date fechaNacimiento;
}
