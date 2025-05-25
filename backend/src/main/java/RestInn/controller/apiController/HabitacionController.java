package RestInn.controller.apiController;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
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

}
