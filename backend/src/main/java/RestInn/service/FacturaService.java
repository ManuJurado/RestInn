package RestInn.service;

import RestInn.entities.cobranzas.Factura;
import RestInn.repositories.FacturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaService {
    private FacturaRepository facturaRepository;
    private ReservaService reservaService;

    public Factura generarFactura(Long reservaId) {

        return null;
    }

    public Factura obtenerFacturaPorId(Long id) {

        return null;
    }

    public List<Factura> obtenerFacturas()  {

        return null;
    }

    public List<Factura> obtenerFacturasPorCliente(Long clienteId) {

        return null;
    }

    public void eliminarFactura(Long id) {

    }
}
