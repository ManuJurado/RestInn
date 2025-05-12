package RestInn.service;

import RestInn.entities.Reserva;
import RestInn.repositories.ReservaRepository;
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

    public List<Reserva> obtenerReservas() {
        return reservaRepository.findAll();
    }

    public Reserva crearReserva(Reserva reserva) {
        validarFechas(reserva);
        return reservaRepository.save(reserva);
    }

    public Reserva obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reserva con ID " + id + " no encontrada."
                ));
    }

    public Optional<Reserva> actualizarReserva(Long id, Reserva datos) {
        validarFechas(datos);
        return reservaRepository.findById(id)
                .map(existing -> {
                    existing.setFechaIngreso(datos.getFechaIngreso());
                    existing.setFechaSalida(datos.getFechaSalida());
                    return reservaRepository.save(existing);
                });
    }

    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }

    private void validarFechas(Reserva reserva) {
        if (reserva.getFechaIngreso() == null || reserva.getFechaSalida() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las fechas de ingreso y salida son obligatorias."
            );
        }
        if (reserva.getFechaIngreso().isAfter(reserva.getFechaSalida())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de ingreso no puede ser posterior a la fecha de salida."
            );
        }
    }
}
