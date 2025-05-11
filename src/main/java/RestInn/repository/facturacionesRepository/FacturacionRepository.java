package RestInn.repository.facturacionesRepository;

import RestInn.entity.facturaciones.Facturacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturacionRepository extends JpaRepository<Facturacion, Long> {
}