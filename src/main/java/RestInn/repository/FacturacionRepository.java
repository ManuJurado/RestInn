package RestInn.repository;

import RestInn.entity.Facturacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturacionRepository extends JpaRepository<Facturacion, Long> {
}