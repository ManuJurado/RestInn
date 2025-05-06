package RestInn.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class EmpleadoDTO {
    private Long id;
    private String nombre;
    private String cargo;
    private String hotelAsignado;
}