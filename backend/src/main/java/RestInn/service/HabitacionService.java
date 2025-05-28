package RestInn.service;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.entities.Habitacion;
import RestInn.entities.enums.H_Estado;
import RestInn.exceptions.BadRequestException;
import RestInn.repositories.HabitacionRepository;
import RestInn.repositories.specifications.HabitacionSprecification;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class HabitacionService {
    private final HabitacionRepository habitacionRepository;
    private final ReservaService reservaService;

    @Autowired
    public HabitacionService(HabitacionRepository habitacionRepository,@Lazy ReservaService reservaService) {
        this.habitacionRepository = habitacionRepository;
        this.reservaService = reservaService;
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

    public Habitacion convertirAEntidad(HabitacionResponseDTO dto) {
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

    public Optional<HabitacionResponseDTO> buscarDTOPorId(Long id) {
        Optional<Habitacion> hab = habitacionRepository.findById(id);
        if (hab.isPresent()) {
            return Optional.ofNullable(convertirAResponseDTO(hab.get()));
        } else {
            throw new BadRequestException("El id de la habitación no existe.");
        }
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
                .piso(habitacion.getPiso())
                .capacidad(habitacion.getCapacidad())
                .estado(habitacion.getEstado())
                .tipo(habitacion.getTipo())
                .precioNoche(habitacion.getPrecioNoche())
                .comentario(habitacion.getComentario())
                .build();
    }

    public List<HabitacionResponseDTO> buscarHabitaciones (H_Estado tipo, Integer capacidad, Double precioNoche, Integer cantCamas) {
        Specification<Habitacion> spec = Specification
                .where (HabitacionSprecification.tieneTipo (tipo))
                .and (HabitacionSprecification.tieneCapacidad (capacidad))
                .and (HabitacionSprecification.precioNocheMenorA(precioNoche))
                .and (HabitacionSprecification.tieneCantCamas(cantCamas));
                // La consulta se ejecuta con los filtros aplicados
        return habitacionRepository.findAll(spec).stream().map(this::convertirAResponseDTO).toList();
    }

    //metodo agregado para obtener lista de habitaciones disponibles en un rango de fechas. Se usa reservaService
    public List<HabitacionResponseDTO> obtenerHabitacionesDisponibles(LocalDate ingreso, LocalDate salida) {

        // IDs ocupadas delegadas al service de reservas
        Set<Long> ocupadasIds = reservaService.obtenerIdsHabitacionesOcupadas(ingreso, salida);

        // Quedarnos sólo con las libres
        return listarTodas().stream()
                .filter(dto -> !ocupadasIds.contains(dto.getId()))
                .toList();
    }

    public List<HabitacionResponseDTO> habitacionesReservables() {
        return habitacionRepository.findByEstadoNot(H_Estado.MANTENIMIENTO)
                .stream()
                .map(this::convertirAResponseDTO).toList();
    }
}