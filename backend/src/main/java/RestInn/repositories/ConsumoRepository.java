package RestInn.repositories;

import RestInn.entities.cobranzas.Consumo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumoRepository extends JpaRepository<Consumo, Long> {

}