package RestInn.controller.apiController;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.entities.enums.H_Estado;
import RestInn.service.HabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // permite peticiones desde el frontend local
public class HabitacionController {
    @Autowired
    private final HabitacionService habitacionService;

    @GetMapping
    public List<HabitacionResponseDTO> listarHabitaciones() {
        return habitacionService.listarTodas();
    }

    @GetMapping ("/filtrar")
    public List<HabitacionResponseDTO> filtrarHabitaciones(
            @RequestParam(required = false) H_Estado tipo,
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) Double precioNoche,
            @RequestParam(required = false) Integer cantCamas) {
        return habitacionService.buscarHabitaciones (tipo, capacidad, precioNoche, cantCamas);
    }

    @GetMapping ("/reservables")
    public List<HabitacionResponseDTO> mostrarHabitacionesReservables() {
        return habitacionService.habitacionesReservables();
    }

    //Get para recibir lsita de habitaciones disponibles en un rango de fechas. Considera las habitaciones que no esten reservadas dentro de el rango establecido
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

    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public HabitacionResponseDTO crearHabitacion(@RequestBody @Valid HabitacionRequestDTO dto) {
        return habitacionService.crearHabitacion(dto);
    }
}
