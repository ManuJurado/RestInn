package RestInn.dto.reservasDTO;

import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Getter @Setter @NoArgsConstructor
@AllArgsConstructor @Builder
public class ReservaResponseDTO {
        private Long id;
        private LocalDate fechaIngreso;
        private LocalDate fechaSalida;
        private LocalDate fechaReserva;
        private UsuarioResponseDTO usuario;      // <— Nuevo campo
        private Long habitacionId;
        private String estado;
        private Integer habitacionNumero;
        private List<HuespedResponseDTO> huespedes;
}
