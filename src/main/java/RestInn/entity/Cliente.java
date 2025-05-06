package RestInn.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    @Id
    private Long id;
    private String nombre;
    private String correo;
    private String telefono;
}