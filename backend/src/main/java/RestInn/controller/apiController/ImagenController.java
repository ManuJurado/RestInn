package RestInn.controller.apiController;

import RestInn.entities.Imagen;
import RestInn.service.ImagenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/imagenes")
public class ImagenController {

    @Autowired
    private ImagenService imagenService;

    @PostMapping("/subir")
    public ResponseEntity<String> subirImagen(@RequestParam("archivo") MultipartFile archivo) {
        try {
            Imagen img = imagenService.guardarImagen(archivo);
            return ResponseEntity.ok("Imagen guardada con ID: " + img.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar imagen");
        }
    }

    @GetMapping("/ver/{id}")
    public ResponseEntity<byte[]> verImagen(@PathVariable Long id) {
        Imagen img = imagenService.obtenerImagen(id);
        if (img != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(img.getTipo()))
                    .body(img.getDatos());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}