package RestInn.controller.apiController;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/usuarios")
public class RegistroController {
    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Registro de clientes (público)
    @PostMapping("/clientes")
    public ResponseEntity<UsuarioResponseDTO> crearCliente(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO nuevoCliente = usuarioService.crearCliente(dto);
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }

//    // Crear admin (opcional, podés protegerlo si querés con rol de superadmin)
//    @PostMapping("/admin")
//    public ResponseEntity<UsuarioResponseDTO> crearAdmin(@RequestBody UsuarioRequestDTO dto) {
//        UsuarioResponseDTO nuevoAdmin = usuarioService.crearAdministrador(dto);
//        return new ResponseEntity<>(nuevoAdmin, HttpStatus.CREATED);
//    }
}
