package RestInn.controller.apiController;

import RestInn.entities.Imagen;
import RestInn.service.ImagenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/imagenes")
public class ImagenController {

    @Autowired
    private ImagenService imagenService;

    @PostMapping("/subir")
    public ResponseEntity<String> subirImagen(@RequestParam("archivo") MultipartFile archivo) {
        try {
            imagenService.guardarImagen(archivo);
            return ResponseEntity.ok("Imagen guardada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar imagen");
        }
    }

    @GetMapping("/ver/{id}")
    public List<Imagen> verImagenes(@PathVariable Long id) {
        return imagenService.obtenerImagenesPorHabitacion(id);
    }
}