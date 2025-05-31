package RestInn.controller.apiController;

import RestInn.dto.reservasDTO.ReservaRequestDTO;
import RestInn.dto.reservasDTO.ReservaResponseDTO;
import RestInn.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // ====================================================
    // 1) CREAR RESERVA (CLIENTE, EMPLEADO O ADMIN AUTENTICADO)
    // ====================================================
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE','RECEPCIONISTA','CONSERJE','LIMPIEZA','ADMINISTRADOR')")
    public ResponseEntity<ReservaResponseDTO> crearReserva(
            @Valid @RequestBody ReservaRequestDTO dto) {
        ReservaResponseDTO nueva = reservaService.crearReservaComoUsuarioAutenticado(dto);
        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }

    // ====================================================
    // 2) LISTAR MIS RESERVAS (solo CLIENTE)
    // ====================================================
    @GetMapping("/mis-reservas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservaResponseDTO>> listarMisReservas() {
        List<ReservaResponseDTO> lista = reservaService.listarReservasDeClienteActual();
        return ResponseEntity.ok(lista);
    }

    // ====================================================
    // 3) CANCELAR RESERVA (solo CLIENTE, estado PENDIENTE)
    // ====================================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }

    // ====================================================
    // 4) LISTAR RESERVAS CON FILTROS (RECEPCIONISTA, ADMINISTRADOR)
    // ====================================================
    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA','ADMINISTRADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> listarReservas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long reservaId) {
        List<ReservaResponseDTO> lista = reservaService.listarReservasConFiltros(estado, reservaId);
        return ResponseEntity.ok(lista);
    }

    // ====================================================
    // 5) CHECK-IN (solo RECEPCIONISTA, ADMINISTRADOR)
    // ====================================================
    @PostMapping("/checkin/{reservaId}")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA','ADMINISTRADOR')")
    public ResponseEntity<ReservaResponseDTO> realizarCheckIn(
            @PathVariable Long reservaId) {
        ReservaResponseDTO dto = reservaService.realizarCheckIn(reservaId);
        return ResponseEntity.ok(dto);
    }

    // ====================================================
    // 6) CHECK-OUT (solo RECEPCIONISTA, ADMINISTRADOR)
    // ====================================================
    @PostMapping("/checkout/{reservaId}")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA','ADMINISTRADOR')")
    public ResponseEntity<ReservaResponseDTO> realizarCheckOut(
            @PathVariable Long reservaId) {
        ReservaResponseDTO dto = reservaService.realizarCheckOut(reservaId);
        return ResponseEntity.ok(dto);
    }

    // ====================================================
    // 7) ACTUALIZAR RESERVA (cualquier usuario autenticado que pase validación interna)
    // ====================================================
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservaResponseDTO> updateReserva(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequestDTO dto) {
        ReservaResponseDTO updated = reservaService.actualizarReservaDesdeDto(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ====================================================
    // 8) ELIMINAR RESERVA (cualquier usuario autenticado que pase validación interna)
    // ====================================================
    @DeleteMapping("/administrador/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteReservaPorAdministrador(@PathVariable Long id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.noContent().build();
    }
}
