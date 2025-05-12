package RestInn.service;

import RestInn.dto.reservasDTO.ReservaRequestDTO;
import RestInn.dto.reservasDTO.ReservaResponseDTO;
import RestInn.entities.Reserva;
import RestInn.repositories.ReservaRepository;
import jakarta.annotation.Nonnull;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    // Obtener todas las reservas
    public List<ReservaResponseDTO> obtenerReservas() {
        List<Reserva> reservas = reservaRepository.findAll();
        return reservas.stream()
                .map(reserva -> new ReservaResponseDTO(reserva.getId(), reserva.getFechaIngreso(), reserva.getFechaSalida()))
                .toList();
    }

    // Crear una nueva reserva desde un DTO
    public ReservaResponseDTO crearReservaDesdeDto(ReservaRequestDTO dto) {
        Reserva reserva = new Reserva();
        reserva.setFechaIngreso(dto.getFechaIngreso());
        reserva.setFechaSalida(dto.getFechaSalida());

        Reserva saved = reservaRepository.save(reserva);
        return new ReservaResponseDTO(saved.getId(), saved.getFechaIngreso(), saved.getFechaSalida());
    }

    // Obtener una reserva por ID
    public ReservaResponseDTO obtenerReservaPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva con ID " + id + " no encontrada."));

        return new ReservaResponseDTO(reserva.getId(), reserva.getFechaIngreso(), reserva.getFechaSalida());
    }

    // Actualizar una reserva desde un DTO
    public ReservaResponseDTO actualizarReservaDesdeDto(Long id, ReservaRequestDTO dto) {
        Optional<Reserva> reservaOptional = reservaRepository.findById(id);
        if (reservaOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada");
        }

        Reserva reserva = reservaOptional.get();
        reserva.setFechaIngreso(dto.getFechaIngreso());
        reserva.setFechaSalida(dto.getFechaSalida());

        Reserva updated = reservaRepository.save(reserva);
        return new ReservaResponseDTO(updated.getId(), updated.getFechaIngreso(), updated.getFechaSalida());
    }

    // Eliminar una reserva por ID
    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }


    // Metodo de validación de fechas (alternativa para la entidad)
    private void validarFechas(Reserva reserva) {
        if (reserva.getFechaIngreso() == null || reserva.getFechaSalida() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las fechas de ingreso y salida son obligatorias.");
        }
        if (reserva.getFechaIngreso().isAfter(reserva.getFechaSalida())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de ingreso no puede ser posterior a la fecha de salida.");
        }
    }
}
