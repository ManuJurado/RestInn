package RestInn.controller.apiController;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminUsuarioController {
    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //region Listar todos los usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.verUsuarios());
    }
    //endregion

    //region Listar todos los clientes
    @GetMapping("/clientes")
    public ResponseEntity<List<UsuarioResponseDTO>> listarClientes() {
        return ResponseEntity.ok(usuarioService.verClientes());
    }
    //endregion

    //region Buscar usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }
    //endregion

    //region Crear empleado
    @PostMapping("/empleados")
    public ResponseEntity<UsuarioResponseDTO> crearEmpleado(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO nuevoEmpleado = usuarioService.crearEmpleado(dto);
        return new ResponseEntity<>(nuevoEmpleado, HttpStatus.CREATED);
    }
    //endregion

    //region Modificar cualquier usuario
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> modificarUsuario(@PathVariable Long id, @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO actualizado = usuarioService.modificarUsuario(id, dto);
        return ResponseEntity.ok(actualizado);
    }
    //endregion

    //region Eliminar cualquier usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarUsuario(@PathVariable Long id) {
        usuarioService.borrarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    //endregion
}
