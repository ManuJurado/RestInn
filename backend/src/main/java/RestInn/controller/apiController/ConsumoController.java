package RestInn.controller.apiController;

import RestInn.dto.cobranzasDTO.ConsumoRequestDTO;
import RestInn.dto.cobranzasDTO.ConsumoResponseDTO;
import RestInn.service.ConsumoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consumos")
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
public class ConsumoController {
    @Autowired
    private ConsumoService consumoService;

    //region Crear un nuevo consumo asociado a una factura.
    @PostMapping("/factura/{facturaId}")
    public ResponseEntity<ConsumoResponseDTO> crearConsumo(
            @PathVariable Long facturaId,
            @RequestBody @Valid ConsumoRequestDTO dto) {
        ConsumoResponseDTO response = consumoService.crearConsumo(facturaId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    //endregion

    //region Actualizar un consumo existente.
    @PutMapping("/{id}")
    public ResponseEntity<ConsumoResponseDTO> actualizarConsumo(
            @PathVariable Long id,
            @RequestBody @Valid ConsumoRequestDTO dto) {
        ConsumoResponseDTO response = consumoService.actualizarConsumo(id, dto);
        return ResponseEntity.ok(response);
    }
    //endregion

    //region Eliminar un consumo.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarConsumo(@PathVariable Long id) {
        consumoService.eliminarConsumo(id);
        return ResponseEntity.noContent().build();
    }
    //endregion

    //region Listar consumos por factura.
    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<ConsumoResponseDTO>> listarPorFactura(@PathVariable Long facturaId) {
        return ResponseEntity.ok(consumoService.listarPorFacturaDTO(facturaId));
    }
    //endregion

    //region Listar consumos por reserva.
    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<List<ConsumoResponseDTO>> listarPorReserva(@PathVariable Long reservaId) {
        return ResponseEntity.ok(consumoService.listarPorReservaDTO(reservaId));
    }
    //endregion
}
