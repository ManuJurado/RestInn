package RestInn.repositories;

import RestInn.entities.cobranzas.Consumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsumoRepository extends JpaRepository<Consumo, Long> {
    List<Consumo> findByFacturaId(Long facturaId);
}