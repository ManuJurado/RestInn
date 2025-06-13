package RestInn.controller.apiController;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // region Buscar usuarios por id. Cualquier autenticado puede buscar por ID (pero en el servicio validás si es su propia cuenta o si tiene permisos)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }
    //endregion

    //region Listar Usuarios. Solo ADMINISTRADOR y RECEPCIONISTA pueden ver todos los usuarios(sin su informacion sensible)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RECEPCIONISTA')")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.verUsuarios());
    }
    //endregion

    //region Trae al usuario actual
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioActual(Authentication authentication) {
        String nombreLogin = authentication.getName();
        UsuarioResponseDTO usuario = usuarioService.buscarPorNombreLogin(nombreLogin);
        return ResponseEntity.ok(usuario);
    }
    //endregion

    //region Creacion de un administrador (muy ocasional)
    @PostMapping("/admin")
    public ResponseEntity<UsuarioResponseDTO> crearAdmin(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO nuevoAdmin = usuarioService.crearAdministrador(dto);
        return new ResponseEntity<>(nuevoAdmin, HttpStatus.CREATED);
    }
    //endregion

    //region Modifica un usuario por id. Solo ADMIN o el MISMO USUARIO puede modificar (lo validás en el servicio)
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> modificarUsuario(@PathVariable Long id, @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO actualizado = usuarioService.modificarUsuario(id, dto);
        return ResponseEntity.ok(actualizado);
    }
    //endregion

    //region Borra un usuario. Solo ADMIN puede borrar usuarios o un cliente puede borrar su propio usuario. Contempla el "derecho de adminisión" Ahora mismo solo lo usa el cliente...
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarUsuario(@PathVariable Long id) {
        usuarioService.borrarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    //endregion
}
