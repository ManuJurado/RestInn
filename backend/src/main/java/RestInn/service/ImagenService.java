package RestInn.service;

import RestInn.entities.Habitacion;
import RestInn.entities.Imagen;
import RestInn.exceptions.BadRequestException;
import RestInn.repositories.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ImagenService {

    @Autowired
    private HabitacionService habitacionService;

    @Autowired
    private ImagenRepository imagenRepository;

    //Guarda una nueva imagen y la asocia a la habitación indicada. Lanza ResponseStatusException si la habitación no existe o está inactiva.
    public Imagen guardarImagen(MultipartFile archivo, Long idHabitacion) throws Exception {
        Habitacion habitacion = habitacionService.buscarEntidadPorId(idHabitacion)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Habitación inexistente"));

        // Opcional: verificar que la habitación esté activa antes de asociar la imagen
        if (!Boolean.TRUE.equals(habitacion.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No puede asociar imagen a habitación inactiva");
        }

        Imagen imagen = new Imagen();
        imagen.setNombre(archivo.getOriginalFilename());
        imagen.setTipo(archivo.getContentType());
        imagen.setDatos(archivo.getBytes());
        imagen.setHabitacion(habitacion);
        return imagenRepository.save(imagen);
    }

    // Devuelve todas las imágenes de todas las habitaciones.
    public List<Imagen> obtenerImagenes() {
        return imagenRepository.findAll();
    }

    //Devuelve todas las imágenes asociadas a la habitación indicada. Lanza BadRequestException si la habitación no existe.
    public List<Imagen> obtenerImagenesPorHabitacion(Long idHabitacion) {
        Habitacion habitacion = habitacionService.buscarEntidadPorId(idHabitacion)
                .orElseThrow(() -> new BadRequestException("Habitación inexistente"));
        return habitacion.getImagenes();
    }

    /** Busca una imagen por su ID. */
    public Optional<Imagen> buscarPorId(Long id) {
        return imagenRepository.findById(id);
    }
}
