package RestInn.repositories;

import RestInn.entities.cobranzas.Factura;
import RestInn.entities.enums.TipoFactura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByReservaId(Long reservaId);
    Optional<Factura> findByReservaIdAndTipoFactura(Long reservaId, TipoFactura tipoFactura);
    List<Factura> findByReserva_Usuario_Id(Long clienteId);
}