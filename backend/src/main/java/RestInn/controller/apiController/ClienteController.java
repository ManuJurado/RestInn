package RestInn.controller.apiController;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@PreAuthorize("hasRole('CLIENTE')")
public class ClienteController {
    private final UsuarioService usuarioService;

    public ClienteController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //region Ver datos del cliente actual
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getDatosPersonales(Authentication authentication) {
        String nombreLogin = authentication.getName();
        return ResponseEntity.ok(usuarioService.buscarPorNombreLogin(nombreLogin));
    }
    //endregion

    //region Modificar propios datos
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> modificarDatos(@PathVariable Long id, @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO actualizado = usuarioService.modificarUsuario(id, dto);
        return ResponseEntity.ok(actualizado);
    }
    //endregion

    //region Eliminar su propia cuenta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarMiCuenta(@PathVariable Long id) {
        usuarioService.borrarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    //endregion
}
