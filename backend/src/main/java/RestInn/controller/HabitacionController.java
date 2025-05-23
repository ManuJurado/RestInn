package RestInn.controller;

import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.entities.Habitacion;
import RestInn.service.HabitacionService;
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
}
