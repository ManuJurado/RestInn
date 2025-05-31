package RestInn.controller.apiController;

import RestInn.dto.cobranzasDTO.FacturaResponseDTO;
import RestInn.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class  FacturaController {
    private final FacturaService facturaService;

    @GetMapping
    public List<FacturaResponseDTO> listarFacturas() {
        return facturaService.listarTodas();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<FacturaResponseDTO> getFacturaById(@PathVariable Long id) {
        FacturaResponseDTO facturaDTO = facturaService
                .buscarFacturaDTOPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Factura no encontrada"
                ));
        return ResponseEntity.ok(facturaDTO);
    }


}
