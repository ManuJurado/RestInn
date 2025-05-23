package RestInn.dto.reservasDTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuespedResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private Long telefono;
    private Boolean activo;
}
