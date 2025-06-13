package RestInn.service;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.entities.Habitacion;
import RestInn.entities.Reserva;
import RestInn.entities.enums.H_Estado;
import RestInn.exceptions.BadRequestException;
import RestInn.repositories.HabitacionRepository;
import RestInn.repositories.specifications.HabitacionSprecification;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class HabitacionService {

    //region ATRIBUTOS Y CONSTRUCTOR
    private final HabitacionRepository habitacionRepository;
    private final ReservaService reservaService;

    @Autowired
    public HabitacionService(HabitacionRepository habitacionRepository,
                             @Lazy ReservaService reservaService) {
        this.habitacionRepository = habitacionRepository;
        this.reservaService = reservaService;
    }
    //endregion

    //region Guarda el estado anterior para limpieza
    private final Map<Long, H_Estado> estadoPrevio = new ConcurrentHashMap<>();
    //endregion

    //region 1) CREAR HABITACIÓN (ADMIN)
    public HabitacionResponseDTO crearHabitacion(HabitacionRequestDTO dto) {
        Habitacion habitacion = convertirAEntidad(dto);
        if (habitacion.getActivo() == null) habitacion.setActivo(true);
        if (habitacion.getEstado() == null) habitacion.setEstado(H_Estado.DISPONIBLE);
        Habitacion guardada = habitacionRepository.save(habitacion);
        return convertirAResponseDTO(guardada);
    }
    //endregion

    //region 2) MODIFICAR HABITACIÓN (ADMIN)
    public HabitacionResponseDTO modificarHabitacion(Long id, HabitacionRequestDTO dto) {
        Habitacion existente = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Habitación no encontrada"));

        existente.setTipo(dto.getTipo());
        existente.setNumero(dto.getNumero());
        existente.setPiso(dto.getPiso());
        existente.setCapacidad(dto.getCapacidad());
        existente.setCantCamas(dto.getCantCamas());
        existente.setPrecioNoche(dto.getPrecioNoche());
        existente.setComentario(dto.getComentario());
        existente.setEstado(dto.getEstado());
        // No tocamos “activo” acá

        Habitacion guardada = habitacionRepository.save(existente);
        return convertirAResponseDTO(guardada);
    }
    //endregion

    //region 3) BORRADO LÓGICO (ADMIN)
    public void borrarHabitacion(Long id) {
        Habitacion existente = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Habitación no encontrada"));

        if (reservaService.habitacionTieneReservasVigentesOFuturas(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La habitación no puede ser desactivada porque tiene reservas activas o futuras.");
        }

        existente.setActivo(false);
        habitacionRepository.save(existente);
    }
    //endregion

    //region 4) BUSCAR POR ID PARA PÚBLICO (solo activas)
    public HabitacionResponseDTO buscarDTOPorIdPublic(Long id) {
        Habitacion h = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Habitación no encontrada"));

        if (!Boolean.TRUE.equals(h.getActivo())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada");
        }
        return convertirAResponseDTO(h);
    }
    //endregion

    //region 5) BUSCAR POR ID PARA ADMIN (incluso inactivas)
    public HabitacionResponseDTO buscarDTOPorIdAdmin(Long id) {
        Habitacion h = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Habitación no encontrada"));
        return convertirAResponseDTO(h);
    }
    //endregion

    //region 6) LISTAR HABITACIONES ACTIVAS (público)
    public List<HabitacionResponseDTO> listarActivas() {
        return habitacionRepository.findByActivoTrue().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    //endregion

    //region 7) LISTAR TODAS INCLUIDAS INACTIVAS (ADMIN)
    public List<HabitacionResponseDTO> listarTodasIncluidasInactivas() {
        return habitacionRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    //endregion

    //region 8) CAMBIAR ESTADO DE HABITACIÓN (LIMPIEZA/MANTENIMIENTO)
    public HabitacionResponseDTO cambiarEstadoHabitacion(Long id, H_Estado nuevoEstado) {
        Habitacion existente = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Habitación no encontrada"));

        existente.setEstado(nuevoEstado);
        Habitacion guardada = habitacionRepository.save(existente);
        return convertirAResponseDTO(guardada);
    }
    //endregion

    //region 9) FILTRAR HABITACIONES ACTIVAS (público)
    public List<HabitacionResponseDTO> buscarHabitaciones(
            H_Estado tipo, Integer capacidad, Double precioNoche, Integer cantCamas) {

        Specification<Habitacion> spec = Specification
                .where(HabitacionSprecification.tieneTipo(tipo))
                .and(HabitacionSprecification.tieneCapacidad(capacidad))
                .and(HabitacionSprecification.precioNocheMenorA(precioNoche))
                .and(HabitacionSprecification.tieneCantCamas(cantCamas))
                .and((root, query, cb) -> cb.isTrue(root.get("activo"))); // sólo activas

        return habitacionRepository.findAll(spec).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }
    //endregion

    //region 10) HABITACIONES RESERVABLES (público)
    public List<HabitacionResponseDTO> habitacionesReservables() {
        return habitacionRepository.findAll((root, query, cb) ->
                        cb.and(
                                cb.isTrue(root.get("activo")),
                                cb.notEqual(root.get("estado"), H_Estado.MANTENIMIENTO)
                        )
                ).stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }
    //endregion

    //region 11) HABITACIONES DISPONIBLES EN RANGO (autenticado)
    public List<HabitacionResponseDTO> obtenerHabitacionesDisponibles(
            LocalDate ingreso, LocalDate salida) {

        Set<Long> ocupadasIds = reservaService.obtenerIdsHabitacionesOcupadas(ingreso, salida);

        return habitacionRepository.findByActivoTrue().stream()
                .filter(h -> !ocupadasIds.contains(h.getId()))
                .map(this::convertirAResponseDTO)
                .toList();
    }
    //endregion

    //region 12) ELIMINAR HABITACION (BORRADO LOGICO)
    public void eliminarHabitacion(Long id) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Habitación no encontrada con ID: " + id));
        habitacion.setActivo(false);
        habitacionRepository.save(habitacion);
    }
    //endregion

    //region 13) BUSCAR ENTIDAD CON BLOQUEO (para reserva)
    @Transactional(readOnly = true)
    public Habitacion buscarConBloqueo(Long id) {
        return habitacionRepository.findByIdConBloqueo(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Habitación no encontrada"));
    }
    //endregion

    //region 14) BUSCAR ENTIDAD POR ID (para otros servicios, p.ej. ImagenService)
    public Optional<Habitacion> buscarEntidadPorId(Long id) {
        return habitacionRepository.findById(id);
    }
    //endregion

    //region 15) REACTIVAR (borrado lógico inverso)
    public HabitacionResponseDTO reactivarHabitacion(Long id) {
        Habitacion hab = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Habitación no encontrada"));

        if (Boolean.TRUE.equals(hab.getActivo())) {
            // Ya estaba activa: no tocamos nada
            return convertirAResponseDTO(hab);
        }

        hab.setActivo(true);
        Habitacion guardada = habitacionRepository.save(hab);
        return convertirAResponseDTO(guardada);
    }
    //endregion

    //region CONVERTIDORES ENTIDAD ↔ DTO
    private Habitacion convertirAEntidad(HabitacionRequestDTO dto) {
        return Habitacion.builder()
                .tipo(dto.getTipo())
                .numero(dto.getNumero())
                .piso(dto.getPiso())
                .capacidad(dto.getCapacidad())
                .cantCamas(dto.getCantCamas())
                .precioNoche(dto.getPrecioNoche())
                .comentario(dto.getComentario())
                .estado(dto.getEstado())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
    }

    private HabitacionResponseDTO convertirAResponseDTO(Habitacion h) {
        return HabitacionResponseDTO.builder()
                .id(h.getId())
                .numero(h.getNumero())
                .piso(h.getPiso())
                .capacidad(h.getCapacidad())
                .cantCamas(h.getCantCamas())
                .precioNoche(h.getPrecioNoche())
                .comentario(h.getComentario())
                .estado(h.getEstado())
                .tipo(h.getTipo())
                .activo(h.getActivo())
                .build();
    }
    //endregion

    //region Conserje pone en mantenimiento: solo si activo==true y estado != OCUPADA
    @Transactional
    public HabitacionResponseDTO conserjePonerMantenimiento(Long id) {
        Habitacion h = buscarEntidadActiva(id);

        if (h.getEstado() == H_Estado.OCUPADA) {
            throw new BadRequestException("No se puede poner en mantenimiento una habitación ocupada");
        }
        h.setEstado(H_Estado.MANTENIMIENTO);
        return convertirAResponseDTO(habitacionRepository.save(h));
    }
    //endregion

    //region Conserje pone disponible: solo si activo==true y estaba en mantenimiento
    @Transactional
    public HabitacionResponseDTO conserjePonerDisponible(Long id) {
        Habitacion h = buscarEntidadActiva(id);

        if (h.getEstado() != H_Estado.MANTENIMIENTO) {
            throw new BadRequestException("Solo se puede volver a disponible desde mantenimiento");
        }
        h.setEstado(H_Estado.DISPONIBLE);
        return convertirAResponseDTO(habitacionRepository.save(h));
    }
    //endregion

    //region Limpieza pone limpieza: solo si activo==true y estado ∈ {DISPONIBLE,OCUPADA}Guarda estado anterior para poder restaurarlo
    @Transactional
    public HabitacionResponseDTO limpiezaPonerLimpieza(Long id) {
        Habitacion h = buscarEntidadActiva(id);

        H_Estado actual = h.getEstado();
        if (actual != H_Estado.DISPONIBLE && actual != H_Estado.OCUPADA) {
            throw new BadRequestException("Solo se puede poner en limpieza desde ocupada o disponible");
        }
        estadoPrevio.put(id, actual);
        h.setEstado(H_Estado.LIMPIEZA);
        return convertirAResponseDTO(habitacionRepository.save(h));
    }
    //endregion

    //region Limpieza restaura estado previo guardado
    @Transactional
    public HabitacionResponseDTO limpiezaRestaurarEstado(Long id) {
        Habitacion h = buscarEntidadActiva(id);

        H_Estado previo = estadoPrevio.get(id);
        if (previo == null) {
            throw new BadRequestException("No hay estado previo registrado para restaurar");
        }
        h.setEstado(previo);
        estadoPrevio.remove(id);
        return convertirAResponseDTO(habitacionRepository.save(h));
    }
    //endregion

    //region Helper: buscar habitación activa o tirar excepción
    private Habitacion buscarEntidadActiva(Long id) {
        Habitacion h = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Habitación no encontrada"));
        if (!Boolean.TRUE.equals(h.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La habitación no está activa");
        }
        return h;
    }
    //endregion

}
