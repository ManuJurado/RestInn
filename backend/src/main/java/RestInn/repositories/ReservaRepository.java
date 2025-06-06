package RestInn.repositories;

import RestInn.entities.Habitacion;
import RestInn.entities.Reserva;
import RestInn.entities.enums.EstadoReserva;
import RestInn.entities.usuarios.Usuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long usuarioId);
    List<Reserva> findByEstadoReserva(EstadoReserva estadoReserva);

    List<Reserva> findByUsuarioAndFechaIngresoLessThanEqualAndFechaSalidaGreaterThanEqual(
            Usuario usuario, LocalDate fechaFin, LocalDate fechaInicio
    );

    boolean existsByHabitacionAndFechaIngresoLessThanAndFechaSalidaGreaterThan(
            Habitacion habitacion,
            LocalDate fechaSalida,
            LocalDate fechaIngreso
    );

    List<Reserva> findByFechaIngresoLessThanAndFechaSalidaGreaterThan(
            LocalDate fechaSalida, LocalDate fechaIngreso);

    //uso en situaciones críticas (check-in, check-out, concurrencia).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reserva> findWithLockingById(Long id);
}