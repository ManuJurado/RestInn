package RestInn.dto.usuariosDTO;

import RestInn.validation.UsuarioValido;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UsuarioValido
public class UsuarioRequestDTO {
    private String nombre;
    private String apellido;
    private String nombreLogin;
    private String dni;
    private String phoneNumber;
    private String email;
    private String oldPassword;
    private String password;  // Aquí recibimos el password en texto plano para crear/modificar
    private String cuit;
    private Boolean activo;
}
