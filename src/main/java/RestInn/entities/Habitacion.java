package RestInn.entities;

import RestInn.entities.enums.H_Estado;
import RestInn.entities.enums.H_Tipo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean activo;     // Borrado Lógico.
    private Boolean disponible; // Listo para usar.
    private H_Estado estado;
    private H_Tipo tipo;
    private Integer numero, capacidad, cantCamas;
    private Double precioNoche;
    private String comentario;
}
