package RestInn.repositories;

import RestInn.entities.Reserva;
import RestInn.entities.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long usuarioId);
    // devuelve reservas cuyo ingreso ≤ hasta y salida ≥ desde (cualquier solapamiento)
    List<Reserva> findByUsuarioAndFechaIngresoLessThanEqualAndFechaSalidaGreaterThanEqual(
            Usuario usuario, LocalDate fechaFin, LocalDate fechaInicio
    );
}