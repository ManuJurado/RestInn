package RestInn.controller.apiController;

import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.service.HabitacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@PreAuthorize("hasAnyRole('ADMINISTRADOR','RECEPCIONISTA','LIMPIEZA','CONSERJE')")
public class EmpleadosController {

    private final HabitacionService habitacionService;

    public EmpleadosController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    //region Listar todas activas (cualquier empleado autorizado)
    @GetMapping("/habitaciones")
    public ResponseEntity<List<HabitacionResponseDTO>> listarActivas() {
        return ResponseEntity.ok(habitacionService.listarActivas());
    }
    //endregion

    //region Conserje: poner mantenimiento / volver disponible
    @PutMapping("/habitaciones/{id}/estado-mantenimiento")
    @PreAuthorize("hasRole('CONSERJE')")
    public ResponseEntity<HabitacionResponseDTO> ponerMantenimiento(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                habitacionService.conserjePonerMantenimiento(id)
        );
    }
    //endregion

    //region Cambia el estado de una habitacion de mantenimiento a disponible. Solo para conserje
    @PutMapping("/habitaciones/{id}/estado-disponible")
    @PreAuthorize("hasRole('CONSERJE')")
    public ResponseEntity<HabitacionResponseDTO> conserjePonerDisponible(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                habitacionService.conserjePonerDisponible(id)
        );
    }
    //endregion

    //region Limpieza: poner limpieza / volver a estado anterior
    @PreAuthorize("hasRole('LIMPIEZA')")
    @PutMapping("/habitaciones/{id}/estado-limpieza")
    public ResponseEntity<HabitacionResponseDTO> ponerLimpieza(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                habitacionService.limpiezaPonerLimpieza(id)
        );
    }
    //endregion

    //region Restaura el estado de limpieza a su anterior estado de una habitacion
    @PutMapping("/habitaciones/{id}/restaurar-estado")
    @PreAuthorize("hasRole('LIMPIEZA')")
    public ResponseEntity<HabitacionResponseDTO> limpiezaRestaurarEstado(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                habitacionService.limpiezaRestaurarEstado(id)
        );
    }
    //endregion
}
