package RestInn.service;

import RestInn.dto.reservasDTO.HuespedResponseDTO;
import RestInn.dto.reservasDTO.ReservaRequestDTO;
import RestInn.dto.reservasDTO.ReservaResponseDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.entities.Huesped;
import RestInn.entities.Reserva;
import RestInn.entities.Habitacion;
import RestInn.entities.cobranzas.Factura;
import RestInn.entities.enums.EstadoFactura;
import RestInn.entities.usuarios.Usuario;
import RestInn.entities.enums.EstadoReserva;
import RestInn.exceptions.ReservaNoDisponibleException;
import RestInn.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioService usuarioService;
    private final HabitacionService habitacionService;
    private final FacturaService facturaService;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioService usuarioService,
                          HabitacionService habitacionService, FacturaService facturaService) {
        this.reservaRepository = reservaRepository;
        this.usuarioService = usuarioService;
        this.habitacionService = habitacionService;
        this.facturaService = facturaService;
    }

    // ====================================================
    // MÉTODOS AUXILIARES (internos)
    // ====================================================

    //region Lógica interna para crear reserva, asignada a cualquier Usuario (@Transactional). Bloquea la habitación, valida solapamientos y persiste la reserva.
    @Transactional(rollbackFor = Exception.class)
    protected ReservaResponseDTO crearReservaDesdeDto(ReservaRequestDTO dto, Usuario usuario) {
        // 1) Validar existencia de usuario
        usuario = usuarioService
                .buscarEntidadPorNombreLogin(usuario.getNombreLogin())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 2) Bloqueo pesimista: cargar habitación con lock
        Habitacion habitacion = habitacionService.buscarConBloqueo(dto.getHabitacionId());
        if (habitacion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada");
        }

        // 3) Verificar solapamiento SOLO con reservas “activas”
        List<EstadoReserva> estadosActivos = List.of(
                EstadoReserva.PENDIENTE,
                EstadoReserva.CONFIRMADA,
                EstadoReserva.EN_CURSO
        );

        boolean ocupado = reservaRepository
                .existsByHabitacionAndEstadoReservaInAndFechaIngresoLessThanAndFechaSalidaGreaterThan(
                        habitacion,
                        estadosActivos,
                        dto.getFechaSalida(),
                        dto.getFechaIngreso()
                );

        if (ocupado) {
            throw new ReservaNoDisponibleException(
                    "La habitación ya está reservada en esas fechas");
        }

        // 4) Construir entidad Reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);
        reserva.setFechaIngreso(dto.getFechaIngreso());
        reserva.setFechaSalida(dto.getFechaSalida());
        reserva.setFechaReserva(dto.getFechaReserva() != null
                ? dto.getFechaReserva()
                : LocalDate.now());
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);

        // 5) Mapear huéspedes
        List<Huesped> huespedes = dto.getHuespedes().stream()
                .map(hdto -> {
                    Huesped h = new Huesped();
                    h.setNombre(hdto.getNombre());
                    h.setApellido(hdto.getApellido());
                    h.setDni(hdto.getDni());
                    return h;
                }).collect(Collectors.toList());
        reserva.setHuespedes(huespedes);

        // 6) Guardar y devolver DTO
        Reserva guardada = reservaRepository.save(reserva);

        // Generar la factura “RESERVA” automáticamente
        // (efectivo, 1 cuota, sin interés/descuento):
        facturaService.generarFacturaReserva(guardada.getId());

        return mapReservaAResponseDTO(guardada);
    }
    //endregion

    //region Convierte una entidad Reserva a ReservaResponseDTO.
    private ReservaResponseDTO mapReservaAResponseDTO(Reserva reserva) {
        List<HuespedResponseDTO> huespedes = reserva.getHuespedes() != null
                ? reserva.getHuespedes().stream()
                .map(h -> {
                    HuespedResponseDTO dto = new HuespedResponseDTO();
                    dto.setNombre(h.getNombre());
                    dto.setApellido(h.getApellido());
                    dto.setDni(h.getDni());
                    return dto;
                }).collect(Collectors.toList())
                : List.of();

        UsuarioResponseDTO usrDto = usuarioService.mapToResponse(reserva.getUsuario());

        return ReservaResponseDTO.builder()
                .id(reserva.getId())
                .fechaIngreso(reserva.getFechaIngreso())
                .fechaSalida(reserva.getFechaSalida())
                .fechaReserva(reserva.getFechaReserva())
                .usuario(usrDto)                           // ← aquí
                .habitacionId(reserva.getHabitacion().getId())
                .estado(reserva.getEstadoReserva().name())
                .habitacionNumero(reserva.getHabitacion().getNumero())
                .huespedes(huespedes)
                .build();
    }
    //endregion

    //region CREAR RESERVA (cliente, empleado o admin autenticado)
    @Transactional
    public ReservaResponseDTO crearReservaComoUsuarioAutenticado(ReservaRequestDTO dto) {
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioService
                .buscarEntidadPorNombreLogin(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Delegar a la lógica interna
        return crearReservaDesdeDto(dto, usuario);
    }
    //endregion

    //region LISTAR RESERVAS DEL USUARIO AUTENTICADO
    public List<ReservaResponseDTO> listarReservasDeClienteActual() {
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioService
                .buscarEntidadPorNombreLogin(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Filtrar por usuario.id
        return reservaRepository.findByUsuarioId(usuario.getId()).stream()
                .map(this::mapReservaAResponseDTO)
                .collect(Collectors.toList());
    }
    //endregion

    //region LISTAR RESERVAS CON FILTROS (para recepcionista/admin)
    public List<ReservaResponseDTO> listarReservasConFiltros(String estado, Long reservaId) {
        // Si viene reservaId, devolver solo esa reserva
        if (reservaId != null) {
            Reserva reserva = reservaRepository.findById(reservaId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Reserva con ID " + reservaId + " no encontrada."));
            return List.of(mapReservaAResponseDTO(reserva));
        }

        // Si viene estado, parsear y filtrar
        if (estado != null) {
            EstadoReserva er;
            try {
                er = EstadoReserva.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Estado inválido: " + estado);
            }
            return reservaRepository.findByEstadoReserva(er).stream()
                    .map(this::mapReservaAResponseDTO)
                    .collect(Collectors.toList());
        }

        // Si no hay parámetros, devolver todas
        return reservaRepository.findAll().stream()
                .map(this::mapReservaAResponseDTO)
                .collect(Collectors.toList());
    }
    //endregion

    //region OBTENER TODAS LAS RESERVAS
    public List<ReservaResponseDTO> obtenerReservas() {
        return reservaRepository.findAll().stream()
                .map(this::mapReservaAResponseDTO)
                .collect(Collectors.toList());
    }
    //endregion

    //region OBTENER RESERVA POR ID
    public ReservaResponseDTO obtenerReservaPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reserva con ID " + id + " no encontrada."));
        return mapReservaAResponseDTO(reserva);
    }
    //endregion

    //region OBTENER RESERVAS POR USUARIO
    public List<ReservaResponseDTO> obtenerReservasPorUsuarioId(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapReservaAResponseDTO)
                .collect(Collectors.toList());
    }
    //endregion

    //region OBTENER RESERVAS ENTRE FECHAS
    public List<ReservaResponseDTO> buscarReservasEntreFechas(Usuario usuario, LocalDate desde, LocalDate hasta) {
        return reservaRepository
                .findByUsuarioAndFechaIngresoLessThanEqualAndFechaSalidaGreaterThanEqual(usuario, desde, hasta)
                .stream()
                .map(this::mapReservaAResponseDTO)
                .collect(Collectors.toList());
    }
    //endregion

    //region OBTENER IDS DE HABITACIONES OCUPADAS
    public Set<Long> obtenerIdsHabitacionesOcupadas(LocalDate ingreso, LocalDate salida) {
        return reservaRepository
                .findByFechaIngresoLessThanAndFechaSalidaGreaterThan(salida, ingreso)
                .stream()
                .filter(r -> r.getEstadoReserva() == EstadoReserva.PENDIENTE   ||
                        r.getEstadoReserva() == EstadoReserva.CONFIRMADA ||
                        r.getEstadoReserva() == EstadoReserva.EN_CURSO)
                .map(r -> r.getHabitacion().getId())
                .collect(Collectors.toSet());
    }
    //endregion

    //region ACTUALIZAR RESERVA (fechas / huéspedes)
    public ReservaResponseDTO actualizarReservaDesdeDto(Long id, ReservaRequestDTO dto) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        reserva.setFechaIngreso(dto.getFechaIngreso());
        reserva.setFechaSalida(dto.getFechaSalida());
        // Si se desean actualizar huéspedes, implementar aquí
        Reserva actualizada = reservaRepository.save(reserva);
        return mapReservaAResponseDTO(actualizada);
    }
    //endregion

    //region ELIMINAR RESERVA
    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }
    //endregion

    //region CANCELAR RESERVA
    @Transactional
    public void cancelarReserva(Long reservaId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        boolean esPropia = reserva.getUsuario().getNombreLogin().equals(username);
        boolean esPrivilegiado = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().matches("ROLE_ADMINISTRADOR|ROLE_RECEPCIONISTA"));

        if (!esPropia && !esPrivilegiado)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No puedes cancelar reservas de otros usuarios");

        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden cancelar reservas en estado PENDIENTE");

        long horasHastaIngreso = ChronoUnit.HOURS.between(
                LocalDateTime.now(),
                reserva.getFechaIngreso().atStartOfDay());

        if (horasHastaIngreso < 48)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La reserva ya no puede cancelarse (menos de 48 h para el ingreso)");

        // 3) Marcar reserva como CANCELADA
        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        // 4) Actualizar estado factura a ANULADA usando FacturaService
        List<Factura> facturas = reserva.getFactura(); // o getFacturas()
        if (facturas != null && !facturas.isEmpty()) {
            Factura factura = facturas.get(0);  // por ejemplo la primera
            factura.setEstado(EstadoFactura.ANULADA);
            facturaService.actualizarFacturaEstado(factura);
        }
    }

    //endregion

    //region CHECK-IN (cambia de CONFIRMADA a EN_CURSO)
    @Transactional
    public ReservaResponseDTO realizarCheckIn(Long reservaId) {
        Reserva reserva = reservaRepository.findWithLockingById(reservaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        // Solo si está confirmada
        if (reserva.getEstadoReserva() != EstadoReserva.CONFIRMADA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se puede hacer check-in de reservas confirmadas.");
        }

        LocalDate hoy = LocalDate.now();
        LocalDate ingreso = reserva.getFechaIngreso();
        LocalDate salida  = reserva.getFechaSalida();

        // No antes de la fecha de ingreso
        if (hoy.isBefore(ingreso)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede hacer check-in antes de la fecha de ingreso.");
        }
        // Ni después de la fecha de salida
        if (hoy.isAfter(salida)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede hacer check-in después de la fecha de salida.");
        }

        // Cambio de estado y persistencia
        reserva.setEstadoReserva(EstadoReserva.EN_CURSO);
        Reserva actualizada = reservaRepository.save(reserva);
        return mapReservaAResponseDTO(actualizada);
    }
    //endregion

    //region  CHECK-OUT (cambia de EN_CURSO a FINALIZADA)
    @Transactional
    public ReservaResponseDTO realizarCheckOut(Long reservaId) {
        Reserva reserva = reservaRepository.findWithLockingById(reservaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        if (reserva.getEstadoReserva() != EstadoReserva.EN_CURSO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se puede hacer check-out de reservas en curso.");
        }

        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(reserva.getFechaSalida())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede hacer check-out antes de la fecha de salida.");
        }

        reserva.setEstadoReserva(EstadoReserva.FINALIZADA);
        Reserva actualizada = reservaRepository.save(reserva);
        return mapReservaAResponseDTO(actualizada);
    }
    //endregion

    //region OBTENER UNA RESERVA POR ID
    public Reserva obtenerPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
    }
    //endregion

    //region CONFIRMAR UNA RESERVA (SE MARCA COMO CONFIRMADA CUANDO FUE PAGADA PARA PODER HACER EL CHECKIN)
    @Transactional
    public ReservaResponseDTO confirmarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden confirmar reservas en estado PENDIENTE");
        }

        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
        Reserva updated = reservaRepository.save(reserva);
        return mapReservaAResponseDTO(updated);
    }
    //endregion

    //region Obtiene booleano si habitacion tiene reservas vigentes o futuras
    public boolean habitacionTieneReservasVigentesOFuturas(Long habitacionId) {
        return reservaRepository.existsByHabitacion_IdAndEstadoReservaNotAndFechaSalidaGreaterThanEqual(
                habitacionId,
                EstadoReserva.CANCELADA,
                LocalDate.now()
        );
    }
    //endregion
}
