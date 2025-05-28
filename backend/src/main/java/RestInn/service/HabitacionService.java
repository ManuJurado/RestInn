package RestInn.service;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.entities.Habitacion;
import RestInn.entities.Imagen;
import RestInn.entities.enums.H_Estado;
import RestInn.entities.usuarios.Usuario;
import RestInn.repositories.HabitacionRepository;
import RestInn.repositories.ImagenRepository;
import RestInn.repositories.specifications.HabitacionSprecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class HabitacionService {
    private final HabitacionRepository habitacionRepository;
    private final ImagenRepository imagenRepository;

    @Autowired
    public HabitacionService(HabitacionRepository habitacionRepository, ImagenRepository imagenRepository) {
        this.habitacionRepository = habitacionRepository;
        this.imagenRepository = imagenRepository;
    }


    public HabitacionResponseDTO crearHabitacion(HabitacionRequestDTO habReqDTO) {
        Habitacion habitacion = convertirAEntidad(habReqDTO);

        // Por default, si no envían activo/disponible, ponemos true
        if (habitacion.getActivo() == null) habitacion.setActivo(true);
        if (habitacion.getEstado() == null) habitacion.setEstado(H_Estado.DISPONIBLE);

        Habitacion habitacionGuardada = habitacionRepository.save(habitacion);
        return convertirAResponseDTO(habitacionGuardada);
    }

    private Habitacion convertirAEntidad(HabitacionRequestDTO dto) {
        return Habitacion.builder()
                .estado(dto.getEstado())
                .tipo(dto.getTipo())
                .numero(dto.getNumero())
                .piso(dto.getPiso())
                .capacidad(dto.getCapacidad())
                .cantCamas(dto.getCantCamas())
                .precioNoche(dto.getPrecioNoche())
                .comentario(dto.getComentario())
                .activo(dto.getActivo())
                .build();
    }

    public HabitacionResponseDTO modificarHabitacion(Long id, HabitacionRequestDTO habReqDTO){

        return null;
    }

    public void borrarHabitacion(Long id){

    }

    public HabitacionResponseDTO buscarPorId(Long id) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirAResponseDTO(habitacion);
    }

    public Optional<Habitacion> buscarEntidadPorId(Long id) {
        return habitacionRepository.findById(id);
    }

    public void cambiarEstadoHabitacion(Long id,  H_Estado nuevoEstado){

    }


    public List<HabitacionResponseDTO> listarTodas() {
        return habitacionRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    private HabitacionResponseDTO convertirAResponseDTO(Habitacion habitacion) {
        return HabitacionResponseDTO.builder()
                .id(habitacion.getId())
                .numero(habitacion.getNumero())
                .piso(habitacion.getPiso())
                .capacidad(habitacion.getCapacidad())
                .estado(habitacion.getEstado())
                .tipo(habitacion.getTipo())
                .precioNoche(habitacion.getPrecioNoche())
                .comentario(habitacion.getComentario())
                .build();
    }

    public List<Habitacion> buscarHabitaciones (H_Estado tipo, Integer capacidad, Double precioNoche, Integer cantCamas) {
        Specification<Habitacion> spec = Specification
                .where (HabitacionSprecification.tieneTipo (tipo))
                .and (HabitacionSprecification.tieneCapacidad (capacidad))
                .and (HabitacionSprecification.precioNocheMenorA(precioNoche))
                .and (HabitacionSprecification.tieneCantCamas(cantCamas));
// La consulta se ejecuta con los filtros aplicados
        return habitacionRepository.findAll(spec);
    }


    public Imagen guardarImagen(MultipartFile archivo) throws Exception {
        Imagen imagen = new Imagen();
        imagen.setNombre(archivo.getOriginalFilename());
        imagen.setTipoImagen(archivo.getContentType());
        imagen.setDatos(archivo.getBytes());
        return habitacionRepository.saveImagen(imagen);
    }

    public Imagen obtenerImagen(Long id) {
        return habitacionRepository.findByImagenId(id).orElse(null);
    }
}