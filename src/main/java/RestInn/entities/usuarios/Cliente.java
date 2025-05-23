package RestInn.entities.usuarios;

import RestInn.entities.Reserva;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;
import java.util.ArrayList;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Cliente extends Usuario{
    List<Reserva> reservas  = new ArrayList<Reserva>();

}
