package RestInn.dto.usuariosDTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String nombreLogin;
    private String dni;
    private String phoneNumber;
    private String email;
    private String CUIT;
    private Boolean activo;
    // NO incluimos password en el response

    // UsuarioResponseDTO.java
    private String role;
}
