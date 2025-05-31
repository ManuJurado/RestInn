package RestInn.service;

import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.dto.reservasDTO.HuespedRequestDTO;
import RestInn.dto.reservasDTO.HuespedResponseDTO;
import RestInn.dto.reservasDTO.ReservaRequestDTO;
import RestInn.dto.reservasDTO.ReservaResponseDTO;
import RestInn.entities.enums.EstadoReserva;
import RestInn.entities.Habitacion;
import RestInn.entities.Huesped;
import RestInn.entities.Reserva;
import RestInn.entities.usuarios.Usuario;
import RestInn.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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

    // ========================
    // CREACIÓN DE RESERVA
    // ========================

    //Crea una nueva reserva validando disponibilidad de la habitación
    @Transactional(rollbackFor = Exception.class)
    public ReservaResponseDTO crearReservaDesdeDto(ReservaRequestDTO dto, Usuario usuario) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        usuario = usuarioService
                .buscarEntidadPorNombreLogin(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Bloqueo pesimista accediendo desde el service
        Habitacion habitacion = habitacionService.buscarConBloqueo(dto.getHabitacionId());


        boolean ocupado = reservaRepository.existsByHabitacionAndFechaIngresoLessThanAndFechaSalidaGreaterThan(
                habitacion, dto.getFechaSalida(), dto.getFechaIngreso()
        );

        HabitacionResponseDTO habitacionDTO = habitacionService.buscarDTOPorId(dto.getHabitacionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada"));
        Habitacion habitacionEntidad = habitacionService.convertirAEntidad(habitacionDTO);
        if (ocupado) {
            throw new ReservaNoDisponibleException("La habitación ya está reservada en esas fechas");
        }

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);
        reserva.setFechaIngreso(dto.getFechaIngreso());
        reserva.setFechaSalida(dto.getFechaSalida());
        reserva.setFechaReserva(LocalDate.now());
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);

        List<Huesped> huespedes = dto.getHuespedes().stream()
                .map(dtoH -> new Huesped(dtoH.getNombre(), dtoH.getApellido(), dtoH.getDni()))
                .toList();

        reserva.setHuespedes(huespedes);

        Reserva guardada = reservaRepository.save(reserva);

        return mapReservaAResponseDTO(guardada);
    }

    // ========================
    // CONSULTA DE RESERVAS
    // ========================


    //Retorna todas las reservas
    public List<ReservaResponseDTO> obtenerReservas() {
        return reservaRepository.findAll().stream()
                .map(this::mapReservaAResponseDTO)
                .toList();
    }

    //Retorna una reserva por ID
    public ReservaResponseDTO obtenerReservaPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva con ID " + id + " no encontrada."));

        return mapReservaAResponseDTO(reserva);
    }

    //Retorna todas las reservas hechas por un usuario
    public List<ReservaResponseDTO> obtenerReservasPorUsuarioId(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapReservaAResponseDTO)
                .toList();
    }

    //Retorna reservas de un usuario entre dos fechas
    public List<ReservaResponseDTO> buscarReservasEntreFechas(Usuario usuario, LocalDate desde, LocalDate hasta) {
        return reservaRepository.findByUsuarioAndFechaIngresoLessThanEqualAndFechaSalidaGreaterThanEqual(usuario, desde, hasta)
                .stream()
                .map(this::mapReservaAResponseDTO)
                .toList();
    }

    //Retorna IDs de habitaciones ocupadas en un rango de fechas
    public Set<Long> obtenerIdsHabitacionesOcupadas(LocalDate ingreso, LocalDate salida) {
        return reservaRepository
                .findByFechaIngresoLessThanAndFechaSalidaGreaterThan(salida, ingreso)
                .stream()
                .filter(r -> r.getEstadoReserva() != EstadoReserva.CONFIRMADA)
                .map(r -> r.getHabitacion().getId())
                .collect(Collectors.toSet());
    }

    // ========================
    // ACTUALIZACIÓN DE RESERVA
    // ========================

    //Actualiza fechas de una reserva (no toca habitación ni huéspedes)
    public ReservaResponseDTO actualizarReservaDesdeDto(Long id, ReservaRequestDTO dto) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        reserva.setFechaIngreso(dto.getFechaIngreso());
        reserva.setFechaSalida(dto.getFechaSalida());
        // Si querés actualizar huéspedes, hacelo acá
        Reserva actualizada = reservaRepository.save(reserva);
        return mapReservaAResponseDTO(actualizada);
    }

    // ========================
    // ELIMINACIÓN DE RESERVA
    // ========================

    //Elimina una reserva por ID
    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }

    // ========================
    // CHECK-IN Y CHECK-OUT
    // ========================

    //Cambia el estado de la reserva a CONFIRMADA (Check-in)
    public ReservaResponseDTO realizarCheckIn(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se puede hacer check-in de reservas pendientes.");
        }

        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
        Reserva actualizada = reservaRepository.save(reserva);
        return mapReservaAResponseDTO(actualizada);
    }

    //Cambia el estado de la reserva a FINALIZADA (Check-out)
    public ReservaResponseDTO realizarCheckOut(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        if (reserva.getEstadoReserva() != EstadoReserva.CONFIRMADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se puede hacer check-out de reservas confirmadas.");
        }

        reserva.setEstadoReserva(EstadoReserva.FINALIZADA);
        Reserva actualizada = reservaRepository.save(reserva);
        return mapReservaAResponseDTO(actualizada);
    }

    // ========================
    // MAPEO DE ENTIDADES A DTO
    // ========================

    private ReservaResponseDTO mapReservaAResponseDTO(Reserva reserva) {
        List<HuespedResponseDTO> huespedes = reserva.getHuespedes() != null
                ? reserva.getHuespedes().stream()
                .map(h -> {
                    HuespedResponseDTO dto = new HuespedResponseDTO();
                    dto.setNombre(h.getNombre());
                    dto.setApellido(h.getApellido());
                    dto.setDni(h.getDni());
                    return dto;
                }).toList()
                : List.of();

        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getFechaIngreso(),
                reserva.getFechaSalida(),
                reserva.getFechaReserva(),
                reserva.getUsuario().getId(),
                reserva.getHabitacion().getId(),
                reserva.getEstadoReserva().name(),
                reserva.getHabitacion().getNumero(),
                huespedes
        );
    }

    private Huesped mapHuespedRequestDtoAEntidad(HuespedRequestDTO dto) {
        Huesped huesped = new Huesped();
        huesped.setNombre(dto.getNombre());
        huesped.setApellido(dto.getApellido());
        huesped.setDni(dto.getDni());
        return huesped;
    }
}
