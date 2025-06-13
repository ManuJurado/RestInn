package RestInn.controller.apiController;

import RestInn.entities.Imagen;
import RestInn.service.ImagenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    @Autowired
    private ImagenService imagenService;

    //region Subir Imagen
    @PostMapping("/{habitacionId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> subirImagen(@RequestParam("archivo") MultipartFile archivo,
                                              @PathVariable Long habitacionId) {
        try {
            imagenService.guardarImagen(archivo, habitacionId);
            return ResponseEntity.ok("Imagen guardada.");
        } catch (ResponseStatusException e) {
            // Captura las excepciones lanzadas con status HTTP y mensaje personalizado
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            // Para otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar imagen");
        }
    }
    //endregion

    //region Ver Todas las Imágenes de una habitacion
    @GetMapping("/ver/{habitacionId}")
    public ResponseEntity<List<String>> verImagenes(@PathVariable Long habitacionId) {
        List<Imagen> imagenes = imagenService.obtenerImagenesPorHabitacion(habitacionId);
        List<String> urls = imagenes.stream()
                .map(imagen -> imagen.getId() + "::/api/imagenes/ver/una/" + imagen.getId())
                .toList();
        return ResponseEntity.ok(urls);
    }
    //endregion

    //region Ver imagen de muestra de una Habitación
    @GetMapping("/ver/una/{id}")
    public ResponseEntity<byte[]> verImagen(@PathVariable Long id) {
        Imagen imagen = imagenService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(imagen.getTipo()));
        headers.setContentLength(imagen.getDatos().length);
        headers.setContentDisposition(ContentDisposition.inline().filename(imagen.getNombre()).build());

        return new ResponseEntity<>(imagen.getDatos(), headers, HttpStatus.OK);
    }
    //endregion

    //region Borrar una imagen de una habitacion existente por id de imagen
    @DeleteMapping("/borrar/{habitacionId}/{imagenId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> borrarImagen(@PathVariable Long habitacionId,
                                               @PathVariable Long imagenId) {
        try {
            imagenService.borrarImagen(imagenId, habitacionId);
            return ResponseEntity.ok("Imagen borrada con éxito");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al borrar la imagen");
        }
    }
    //endregion

}