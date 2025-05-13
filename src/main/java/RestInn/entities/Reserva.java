package RestInn.entities;

import jakarta.persistence.*;
import jakarta.validation.OverridesAttribute;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false
    )
    //ver como funciona overrideatribute para cambiar nombre en tabla
    private LocalDate fechaIngreso;

    @Column(
            nullable = false
    )
    private LocalDate fechaSalida;

}
