package RestInn.repository.reservasRepository;

import RestInn.entity.reservas.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}