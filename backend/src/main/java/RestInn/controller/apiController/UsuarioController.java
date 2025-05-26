package RestInn.controller.apiController;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioActual(Authentication authentication) {
        String nombreLogin = authentication.getName();
        UsuarioResponseDTO usuario = usuarioService.buscarPorNombreLogin(nombreLogin);
        return ResponseEntity.ok(usuario);
    }


    // Solo ADMIN puede crear EMPLEADOS
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/empleados")
    public ResponseEntity<UsuarioResponseDTO> crearEmpleado(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO nuevoEmpleado = usuarioService.crearEmpleado(dto);
        return new ResponseEntity<>(nuevoEmpleado, HttpStatus.CREATED);
    }

    // Cualquiera puede registrarse como CLIENTE (sin login)
    @PostMapping("/clientes")
    public ResponseEntity<UsuarioResponseDTO> crearCliente(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO nuevoCliente = usuarioService.crearCliente(dto);
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }

    // Solo ADMIN puede crear otros ADMINISTRADORES
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/administradores")
    public ResponseEntity<UsuarioResponseDTO> crearAdministrador(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO nuevoAdmin = usuarioService.crearAdministrador(dto);
        return new ResponseEntity<>(nuevoAdmin, HttpStatus.CREATED);
    }

    // Solo ADMIN o el MISMO USUARIO puede modificar (lo validás en el servicio)
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> modificarUsuario(@PathVariable Long id, @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO actualizado = usuarioService.modificarUsuario(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    // Solo ADMIN puede borrar usuarios
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarUsuario(@PathVariable Long id) {
        usuarioService.borrarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // Solo ADMIN y EMPLEADO pueden ver todos los usuarios
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.verUsuarios());
    }

    // Cualquier autenticado puede buscar por ID (pero en el servicio validás si es su propia cuenta o si tiene permisos)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }
}
