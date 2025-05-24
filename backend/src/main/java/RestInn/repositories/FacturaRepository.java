package RestInn.repositories;

import RestInn.entities.cobranzas.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
}