package RestInn.service;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.entities.Habitacion;
import RestInn.entities.enums.H_Estado;
import RestInn.entities.usuarios.Usuario;
import RestInn.repositories.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HabitacionService {
    private final HabitacionRepository habitacionRepository;

    @Autowired
    public HabitacionService(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    public HabitacionResponseDTO crearHabitacion(HabitacionRequestDTO habReqDTO) {
        Habitacion habitacion = convertirAEntidad(habReqDTO);

        // Por default, si no envían activo/disponible, ponemos true
        if (habitacion.getActivo() == null) habitacion.setActivo(true);
        if (habitacion.getDisponible() == null) habitacion.setDisponible(true);

        Habitacion habitacionGuardada = habitacionRepository.save(habitacion);
        return convertirAResponseDTO(habitacionGuardada);
    }

    private Habitacion convertirAEntidad(HabitacionRequestDTO dto) {
        return Habitacion.builder()
                .estado(dto.getEstado())
                .tipo(dto.getTipo())
                .numero(dto.getNumero())
                .capacidad(dto.getCapacidad())
                .cantCamas(dto.getCantCamas())
                .precioNoche(dto.getPrecioNoche())
                .comentario(dto.getComentario())
                .activo(dto.getActivo())
                .disponible(dto.getDisponible())
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

    public List<HabitacionResponseDTO> obtenerHabitacionesDisponibles(){

        return List.of();
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
                .capacidad(habitacion.getCapacidad())
                .estado(habitacion.getEstado())
                .tipo(habitacion.getTipo())
                .precioNoche(habitacion.getPrecioNoche())
                .comentario(habitacion.getComentario())
                .build();
    }


}