package RestInn.dto.usuariosDTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRequestDTO {
    private String nombre;
    private String apellido;
    private String nombreLogin;
    private String dni;
    private String phoneNumber;
    private String email;
    private String password;  // Aquí recibimos el password en texto plano para crear/modificar
    private String CUIT;
    private Boolean activo;
}
