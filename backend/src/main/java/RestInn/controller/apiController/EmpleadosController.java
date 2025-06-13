package RestInn.controller.apiController;

import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//EmpleadosController ahora solo se ocupa de la consulta de empleados,
// puesto que las operaciones sobre habitaciones se han movido a HabitacionController
// y las operaciones sobre reservas están en ReservaController.

@RestController
@RequestMapping("/api/empleados")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'LIMPIEZA', 'CONSERJE')")// endpoints escalables preparados para la inclusion de mas empleados y metodos exclusivos para los mismos en el futuro
public class EmpleadosController {

    private final UsuarioService usuarioService;

    public EmpleadosController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //region CONSULTA DE EMPLEADOS (ADMIN). Solo el administrador puede listar todos los empleados (solo datos NO sensibles).
    @GetMapping("/lista")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarEmpleados() {
        List<UsuarioResponseDTO> lista = usuarioService.verEmpleados();
        return ResponseEntity.ok(lista);
    }
    //endregion

}
