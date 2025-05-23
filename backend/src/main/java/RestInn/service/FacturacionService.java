package RestInn.service;

import RestInn.entities.cobranzas.Facturacion;
import RestInn.repositories.FacturacionRepository;
import RestInn.repositories.ReservaRepository;

import java.util.List;

public class FacturacionService {
    private FacturacionRepository facturacionRepository;
    private ReservaService reservaService;

    public Facturacion generarFactura(Long reservaId) {

        return null;
    }

    public Facturacion obtenerFacturaPorId(Long id) {

        return null;
    }

    public List<Facturacion> obtenerFacturas()  {

        return null;
    }

    public List<Facturacion> obtenerFacturasPorCliente(Long clienteId) {

        return null;
    }

    public void eliminarFactura(Long id) {

    }
}
