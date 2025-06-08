package RestInn.controller.apiController;

import RestInn.dto.cobranzasDTO.FacturaRequestDTO;
import RestInn.dto.cobranzasDTO.FacturaResponseDTO;
import RestInn.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
public class  FacturaController {
    @Autowired
    private FacturaService facturaService;

    //region Crear una factura al crear reserva.
    @PostMapping("/reserva/{reservaId}")
    public ResponseEntity<FacturaResponseDTO> crearFacturaReserva(@PathVariable Long reservaId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facturaService.generarFacturaReserva(reservaId));
    }
    //endregion

    //region Crear factura de consumos al realizar el check-in.
    @PostMapping("/consumos/{reservaId}")
    public ResponseEntity<FacturaResponseDTO> crearFacturaConsumos(@PathVariable Long reservaId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facturaService.generarFacturaConsumos(reservaId));
    }
    //endregion

    //region Emitir la factura de consumos al realizar el check-out.
    @PostMapping("/emitir/{reservaId}")
    public ResponseEntity<FacturaResponseDTO> emitirFacturaConsumos(
            @PathVariable Long reservaId,
            @RequestBody FacturaRequestDTO dto) {
        return ResponseEntity.ok(
                facturaService.emitirFacturaConsumos(reservaId, dto.getMetodoPago(), dto.getCuotas()));
    }
    //endregion

    //region Actualizar una factura.
    @PutMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> actualizarFactura(
            @PathVariable Long id,
            @RequestBody FacturaRequestDTO dto) {
        return ResponseEntity.ok(facturaService.actualizarFactura(id, dto));
    }
    //endregion

    //region Anular una factura.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> anularFactura(@PathVariable Long id) {
        facturaService.anularFactura(id);
        return ResponseEntity.noContent().build();
    }
    //endregion

    //region Listar todas las facturas.
    @GetMapping
    public ResponseEntity<List<FacturaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(facturaService.listarTodasDTO());
    }
    //endregion

    //region Listar las facturas asociadas a una reserva.
    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<List<FacturaResponseDTO>> listarPorReserva(@PathVariable Long reservaId) {
        return ResponseEntity.ok(facturaService.listarPorReservaDTO(reservaId));
    }
    //endregion
}
