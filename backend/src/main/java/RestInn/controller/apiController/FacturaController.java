package RestInn.controller.apiController;

import RestInn.dto.cobranzasDTO.FacturaRequestDTO;
import RestInn.dto.cobranzasDTO.FacturaResponseDTO;
import RestInn.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class  FacturaController {
    @Autowired
    private FacturaService facturaService;

    //region Crear una factura al crear reserva.
    @PostMapping("/reserva/{reservaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RECEPCIONISTA')")
    public ResponseEntity<FacturaResponseDTO> crearFacturaReserva(@PathVariable Long reservaId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facturaService.generarFacturaReserva(reservaId));
    }
    //endregion

    //region Crear factura de consumos al realizar el check-in.
    @PostMapping("/consumos/{reservaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RECEPCIONISTA')")
    public ResponseEntity<FacturaResponseDTO> crearFacturaConsumos(@PathVariable Long reservaId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facturaService.generarFacturaConsumos(reservaId));
    }
    //endregion

    //region Emitir la factura de consumos al realizar el check-out.
    @PostMapping("/emitir/{reservaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RECEPCIONISTA')")
    public ResponseEntity<FacturaResponseDTO> emitirFacturaConsumos(
            @PathVariable Long reservaId,
            @RequestBody FacturaRequestDTO dto) {
        return ResponseEntity.ok(
                facturaService.emitirFacturaConsumos(reservaId, dto.getMetodoPago(), dto.getCuotas()));
    }
    //endregion

    //region Actualizar una factura.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<FacturaResponseDTO> actualizarFactura(
            @PathVariable Long id,
            @RequestBody FacturaRequestDTO dto) {
        return ResponseEntity.ok(facturaService.actualizarFactura(id, dto));
    }
    //endregion

    //region Anular una factura.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RECEPCIONISTA')")
    public ResponseEntity<Void> anularFactura(@PathVariable Long id) {
        facturaService.anularFactura(id);
        return ResponseEntity.noContent().build();
    }
    //endregion

    //region Listar todas las facturas.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RECEPCIONISTA')")
    public ResponseEntity<List<FacturaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(facturaService.listarTodasDTO());
    }
    //endregion

    //region Listar las facturas asociadas a una reserva.
    @GetMapping("/listareservas/{reservaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'CLIENTE')")
    public ResponseEntity<List<FacturaResponseDTO>> listarPorReserva(
            @PathVariable Long reservaId) {
        return ResponseEntity.ok(facturaService.listarPorReservaDTO(reservaId));
    }
    //endregion

    //region Generar el PDF de la factura emitida.
    @GetMapping("/{facturaId}/pdf")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'CLIENTE')")
    public ResponseEntity<InputStreamResource> descargarPdf(@PathVariable Long facturaId) {
        byte[] pdfBytes = facturaService.generarPdf(facturaId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename("factura_" + facturaId + ".pdf")
                        .build());

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(new InputStreamResource(new ByteArrayInputStream(pdfBytes)));
    }
    //endregion
}
