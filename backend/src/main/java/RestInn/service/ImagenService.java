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

    public Imagen guardarImagen(MultipartFile archivo, Long idHabitacion) throws Exception {
        Habitacion habitacion = habitacionService.buscarEntidadPorId(idHabitacion)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Habitación inexistente"));

        if (!Boolean.TRUE.equals(habitacion.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No puede asociar imagen a habitación inactiva");
        }

        int cantidadActual = contarImagenesPorHabitacion(idHabitacion);
        if (cantidadActual >= 15) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Límite máximo de 15 imágenes alcanzado para esta habitación");
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

    // Busca una imagen por su ID.
    public Optional<Imagen> buscarPorId(Long id) {
        return imagenRepository.findById(id);
    }

    public int contarImagenesPorHabitacion(Long idHabitacion) {
        return obtenerImagenesPorHabitacion(idHabitacion).size();
    }

    // Borra una imagen por id para una habitacion existente
    public void borrarImagen(Long imagenId, Long habitacionId) {
        Imagen imagen = buscarPorId(imagenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada"));

        if (!imagen.getHabitacion().getId().equals(habitacionId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen no pertenece a la habitación indicada");
        }

        imagenRepository.delete(imagen);
    }

}
