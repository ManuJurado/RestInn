package RestInn.service;

import RestInn.dto.cobranzasDTO.ConsumoResponseDTO;
import RestInn.entities.cobranzas.Consumo;
import RestInn.entities.cobranzas.Factura;
import RestInn.exceptions.BadRequestException;
import RestInn.repositories.ConsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumoService {
    @Autowired
    private ConsumoRepository consumoRepository;
    @Autowired
    private FacturaService facturaService;

    public List<Consumo> obtenerConsumosxFactura(Long idFactura) {
        Factura factura = facturaService.buscarEntidadFacturaPorId(idFactura)
                .orElseThrow(() -> new BadRequestException("Factura inexistente."));
        return factura.getConsumos();
    }
}
