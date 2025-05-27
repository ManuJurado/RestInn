package RestInn.service;

import RestInn.dto.reservasDTO.HuespedRequestDTO;
import RestInn.dto.reservasDTO.ReservaRequestDTO;
import RestInn.dto.reservasDTO.ReservaResponseDTO;
import RestInn.entities.enums.EstadoReserva;
import RestInn.entities.Habitacion;
import RestInn.entities.Huesped;
import RestInn.entities.Reserva;
import RestInn.entities.usuarios.Usuario;
import RestInn.repositories.HabitacionRepository;
import RestInn.repositories.ReservaRepository;
import RestInn.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioService usuarioService;
    private final HabitacionService habitacionService;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioService usuarioService,
                          HabitacionService habitacionService) {
        this.reservaRepository = reservaRepository;
        this.usuarioService = usuarioService;
        this.habitacionService = habitacionService;
    }

    // Obtener todas las reservas (con map a DTO completo)
    public List<ReservaResponseDTO> obtenerReservas() {
        List<Reserva> reservas = reservaRepository.findAll();
        return reservas.stream()
                .map(this::mapReservaAResponseDTO)
                .toList();
    }

    // Crear reserva desde DTO, mapeando huespedes también
    public ReservaResponseDTO crearReservaDesdeDto(ReservaRequestDTO dto) {
        // Validación de fechas la hace el validador @ReservaValida
        Usuario usuario = usuarioService.buscarEntidadPorId(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Habitacion habitacion = habitacionService.buscarEntidadPorId(dto.getHabitacionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada"));

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);
        reserva.setFechaIngreso(dto.getFechaIngreso());
        reserva.setFechaSalida(dto.getFechaSalida());
        reserva.setFechaReserva(LocalDate.now());
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE); // Ajusta según tu lógica

        // Mapear huespedes desde DTO a entidad
        if (dto.getHuespedes() != null && !dto.getHuespedes().isEmpty()) {
            List<Huesped> huespedes = dto.getHuespedes().stream()
                    .map(this::mapHuespedRequestDtoAEntidad)
                    .collect(Collectors.toList());
            reserva.setHuespedes(huespedes);
        }

        Reserva guardada = reservaRepository.save(reserva);

        return mapReservaAResponseDTO(guardada);
    }

    // Obtener reserva por ID
    public ReservaResponseDTO obtenerReservaPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva con ID " + id + " no encontrada."));

        return mapReservaAResponseDTO(reserva);
    }

    // Actualizar reserva desde DTO (sin tocar usuario, habitación ni huespedes por simplicidad)
    public ReservaResponseDTO actualizarReservaDesdeDto(Long id, ReservaRequestDTO dto) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        reserva.setFechaIngreso(dto.getFechaIngreso());
        reserva.setFechaSalida(dto.getFechaSalida());
        // Si querés actualizar huespedes, tendrías que hacerlo acá también.

        Reserva actualizada = reservaRepository.save(reserva);

        return mapReservaAResponseDTO(actualizada);
    }

    // Eliminar reserva por ID
    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }

    // --- Mapeos privados ---

    public List<ReservaResponseDTO> obtenerReservasPorUsuarioId(Long usuarioId) {
        List<Reserva> reservas = reservaRepository.findByUsuarioId(usuarioId);
        return reservas.stream()
                .map(this::mapReservaAResponseDTO)
                .toList();
    }

    private ReservaResponseDTO mapReservaAResponseDTO(Reserva reserva) {
        List<Long> huespedesIds = reserva.getHuespedes() != null
                ? reserva.getHuespedes().stream().map(Huesped::getId).toList()
                : List.of();

        List<Huesped> huespedes = reserva.getHuespedes() != null
                ? reserva.getHuespedes()
                : List.of();

        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getFechaIngreso(),
                reserva.getFechaSalida(),
                reserva.getFechaReserva(),
                reserva.getUsuario().getId(),
                reserva.getHabitacion().getId(),
                huespedesIds,
                reserva.getEstadoReserva().name(),
                reserva.getHabitacion().getNumero(),
                huespedes
        );
    }

    private Huesped mapHuespedRequestDtoAEntidad(HuespedRequestDTO dto) {
        Huesped huesped = new Huesped();
        // Mapea los campos necesarios, ej:
        huesped.setNombre(dto.getNombre());
        huesped.setApellido(dto.getApellido());
        huesped.setDni(dto.getDni());
        // etc, según tu entidad y DTO de huesped
        return huesped;
    }
}
