package RestInn.repositories;

import RestInn.entities.Facturacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturacionRepository extends JpaRepository<Facturacion, Long> {
}