package RestInn.controller.apiController;

import RestInn.entities.cobranzas.Consumo;
import RestInn.service.ConsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/consumos")
public class ConsumoController {
    @Autowired
    private ConsumoService consumoService;

    @GetMapping("/ver/{facturaId}")
    public ResponseEntity<List<Consumo>> verConsumosPorFactura(@PathVariable Long facturaId) {
        return ResponseEntity.ok(consumoService.obtenerConsumosxFactura(facturaId));
    }
}
