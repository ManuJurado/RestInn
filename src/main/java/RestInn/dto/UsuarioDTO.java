package RestInn.dto;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String telefono;
}