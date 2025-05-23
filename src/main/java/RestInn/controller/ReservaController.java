package RestInn.controller;

import RestInn.dto.reservasDTO.ReservaRequestDTO;
import RestInn.dto.reservasDTO.ReservaResponseDTO;
import RestInn.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    @Autowired
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ReservaResponseDTO createReserva(@RequestBody @Valid ReservaRequestDTO dto) {
        return reservaService.crearReservaDesdeDto(dto);
    }

    @PutMapping("/{id}")
    public ReservaResponseDTO updateReserva(@PathVariable Long id, @Valid @RequestBody ReservaRequestDTO dto) {
        return reservaService.actualizarReservaDesdeDto(id, dto);
    }

    @GetMapping
    public List<ReservaResponseDTO> getAllReservas() {
        return reservaService.obtenerReservas();  // Ya viene completo desde el servicio
    }

    @GetMapping("/{id}")
    public ReservaResponseDTO getReservaById(@PathVariable Long id) {
        return reservaService.obtenerReservaPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deleteReserva(@PathVariable Long id) {
        reservaService.eliminarReserva(id);
    }
}
