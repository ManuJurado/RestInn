package RestInn.entities;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Huesped {
    private String nombre;
    private String apellido;
    private String dni;
}

