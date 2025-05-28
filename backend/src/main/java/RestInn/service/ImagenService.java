package RestInn.service;

import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.entities.Habitacion;
import RestInn.entities.Imagen;
import RestInn.exceptions.BadRequestException;
import RestInn.exceptions.InvalidDataException;
import RestInn.repositories.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ImagenService {
    @Autowired
    private HabitacionService habitacionService;
    @Autowired
    private ImagenRepository imagenRepository;


    public Imagen guardarImagen(MultipartFile archivo) throws Exception {
        Imagen imagen = new Imagen();
        imagen.setNombre(archivo.getOriginalFilename());
        imagen.setTipo(archivo.getContentType());
        imagen.setDatos(archivo.getBytes());
        return imagenRepository.save(imagen);
    }

    public List<Imagen> obtenerImagenes() {
        return imagenRepository.findAll();
    }

    public List<Imagen> obtenerImagenesPorHabitacion(Long id) {
        Optional<HabitacionResponseDTO> hab = Optional.of(habitacionService.buscarDTOPorId(id)
                .orElseThrow(() -> new BadRequestException("Habitación inexistente")));
        return hab.get().getImagenes();
    }
}