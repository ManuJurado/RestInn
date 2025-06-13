package RestInn.controller.apiController;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.entities.enums.H_Estado;
import RestInn.service.HabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") //Permite peticiones desde el frontend local
public class HabitacionController {

    private final HabitacionService habitacionService;


    //region 1) LISTAR HABITACIONES ACTIVAS (público)
    @GetMapping
    public List<HabitacionResponseDTO> listarHabitacionesActivas() {
        return habitacionService.listarActivas();
    }
    //endregion

    //region 2) LISTAR TODAS INCLUYENDO INACTIVAS (ADMIN)
    @GetMapping("/admin/todas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<HabitacionResponseDTO> listarTodasHabitacionesAdmin() {
        return habitacionService.listarTodasIncluidasInactivas();
    }
    //endregion

    //region 3) OBTENER HABITACIÓN POR ID PÚBLICO (solo activas)
    @GetMapping("/{id}")
    public ResponseEntity<HabitacionResponseDTO> getHabitacionByIdPublic(@PathVariable Long id) {
        HabitacionResponseDTO dto = habitacionService.buscarDTOPorIdPublic(id);
        return ResponseEntity.ok(dto);
    }
    //endregion

    //region 4) OBTENER HABITACIÓN POR ID ADMIN (incluye inactivas)
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<HabitacionResponseDTO> getHabitacionByIdAdmin(@PathVariable Long id) {
        HabitacionResponseDTO dto = habitacionService.buscarDTOPorIdAdmin(id);
        return ResponseEntity.ok(dto);
    }
    //endregion

    //region 5) CREAR NUEVA HABITACIÓN (solo ADMINISTRADOR)
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<HabitacionResponseDTO> crearHabitacion(
            @RequestBody @Valid HabitacionRequestDTO dto) {
        HabitacionResponseDTO creada = habitacionService.crearHabitacion(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }
    //endregion

    //region 6) MODIFICAR HABITACIÓN (solo ADMINISTRADOR)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<HabitacionResponseDTO> modificarHabitacion(
            @PathVariable Long id,
            @RequestBody @Valid HabitacionRequestDTO dto) {
        HabitacionResponseDTO actualizada = habitacionService.modificarHabitacion(id, dto);
        return ResponseEntity.ok(actualizada);
    }
    //endregion

    //region 7) ELIMINAR HABITACIÓN (borrado lógico - solo ADMINISTRADOR)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarHabitacion(@PathVariable Long id) {
        habitacionService.eliminarHabitacion(id);
        return ResponseEntity.noContent().build();
    }
    //endregion

    //region 8) FILTRAR HABITACIONES ACTIVAS (público)
    @GetMapping("/filtrar")
    public List<HabitacionResponseDTO> filtrarHabitaciones(
            @RequestParam(required = false) H_Estado tipo,
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) Double precioNoche,
            @RequestParam(required = false) Integer cantCamas) {
        return habitacionService.buscarHabitaciones(tipo, capacidad, precioNoche, cantCamas);
    }
    //endregion

    //region 9) HABITACIONES RESERVABLES (público)
    @GetMapping("/reservables")
    public List<HabitacionResponseDTO> mostrarHabitacionesReservables() {
        return habitacionService.habitacionesReservables();
    }
    //endregion

    //region 10) HABITACIONES DISPONIBLES EN RANGO (autenticado)
    @GetMapping("/disponibles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HabitacionResponseDTO>> getDisponibles(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        if (desde == null || hasta == null || !desde.isBefore(hasta)) {
            return ResponseEntity.badRequest().build();
        }

        List<HabitacionResponseDTO> disponibles =
                habitacionService.obtenerHabitacionesDisponibles(desde, hasta);
        return ResponseEntity.ok(disponibles);
    }
    //endregion

    // region 11) REACTIVAR HABITACIÓN (solo ADMINISTRADOR)
    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<HabitacionResponseDTO> reactivarHabitacion(@PathVariable Long id) {
        HabitacionResponseDTO dto = habitacionService.reactivarHabitacion(id);
        return ResponseEntity.ok(dto);
    }
    // endregion

    //region 12) BORRADO LÓGICO CON VALIDACIÓN DE RESERVAS (solo ADMINISTRADOR)
    @PutMapping("/{id}/borrar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> borrarLogicoHabitacion(@PathVariable Long id) {
        habitacionService.borrarHabitacion(id); // contiene la validación de reservas
        return ResponseEntity.noContent().build();
    }
    //endregion


}
