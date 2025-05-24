package RestInn.service;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.entities.Habitacion;
import RestInn.entities.enums.H_Estado;
import RestInn.repositories.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitacionService {
    private final HabitacionRepository habitacionRepository;

    @Autowired
    public HabitacionService(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    public HabitacionResponseDTO crearHabitacion(HabitacionRequestDTO habReqDTO) {

        return null;
    }

    public HabitacionResponseDTO modificarHabitacion(Long id, HabitacionRequestDTO habReqDTO){

        return null;
    }

    public void borrarHabitacion(Long id){

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
