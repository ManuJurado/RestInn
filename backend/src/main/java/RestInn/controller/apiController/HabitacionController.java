package RestInn.controller.apiController;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.entities.Habitacion;
import RestInn.entities.Imagen;
import RestInn.entities.enums.H_Estado;
import RestInn.service.HabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    @GetMapping ("/filtrar")
    public List<Habitacion> filtrarHabitaciones(
            @RequestParam(required = false) H_Estado tipo,
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) Double precioNoche,
            @RequestParam(required = false) Integer cantCamas) {
        return habitacionService.buscarHabitaciones (tipo, capacidad, precioNoche, cantCamas);
    }

    @PostMapping("/subir")
    public ResponseEntity<String> subirImagen(@RequestParam("archivo") MultipartFile archivo) {
        try {
            Imagen img = habitacionService.guardarImagen(archivo);
            return ResponseEntity.ok("Imagen guardada con ID: " + img.getImagen_id());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar imagen");
        }
    }

    @GetMapping("/ver/{id}")
    public ResponseEntity<byte[]> verImagen(@PathVariable Long id) {
        Imagen img = habitacionService.obtenerImagen(id);
        if (img != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(img.getTipoImagen()))
                    .body(img.getDatos());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
