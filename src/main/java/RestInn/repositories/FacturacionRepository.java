package RestInn.repositories;

import RestInn.entities.cobranzas.Facturacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturacionRepository extends JpaRepository<Facturacion, Long> {
}