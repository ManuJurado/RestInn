package RestInn.controller.apiController;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.entities.Habitacion;
import RestInn.entities.enums.H_Estado;
import RestInn.service.HabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habitaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // permite peticiones desde el frontend local
public class HabitacionController {

    private final HabitacionService habitacionService;

    @GetMapping
    public List<HabitacionResponseDTO> listarHabitaciones() {
        return habitacionService.listarTodas();
    }

    @PostMapping
    public HabitacionResponseDTO crearHabitacion(@RequestBody @Valid HabitacionRequestDTO dto) {
        return habitacionService.crearHabitacion(dto);
    }

    @PostMapping("/test")
    public String testPost() {
        return "POST funciona";
    }

    @GetMapping("/filtrar")
    public List<Habitacion> filtrarHabitaciones(
            @RequestParam(required = false) H_Estado tipo,
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) Double precioNoche,
            @RequestParam(required = false) Integer cantCamas) {
        return habitacionService.buscarHabitaciones (tipo, capacidad, precioNoche, cantCamas);
    }
}
