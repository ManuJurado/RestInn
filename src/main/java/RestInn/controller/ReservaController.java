package RestInn.controller;

import RestInn.dto.reservasDTO.ReservaRequestDTO;
import RestInn.dto.reservasDTO.ReservaResponseDTO;
import RestInn.service.ReservaService;
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

    // Crear una nueva reserva desde un DTO
    @PostMapping
    public ReservaResponseDTO createReserva(@RequestBody ReservaRequestDTO dto) {
        return reservaService.crearReservaDesdeDto(dto);
    }

    // Actualizar una reserva existente
    @PutMapping("/{id}")
    public ReservaResponseDTO updateReserva(@PathVariable Long id, @RequestBody ReservaRequestDTO dto) {
        return reservaService.actualizarReservaDesdeDto(id, dto);
    }

    // Obtener todas las reservas (ahora devuelve una lista de ReservaResponseDTO)
    @GetMapping
    public List<ReservaResponseDTO> getAllReservas() {
        return reservaService.obtenerReservas().stream()
                .map(reserva -> new ReservaResponseDTO(reserva.getId(), reserva.getFechaIngreso(), reserva.getFechaSalida()))
                .toList();
    }

    // Obtener una reserva por ID (ahora devuelve ReservaResponseDTO)
    @GetMapping("/{id}")
    public ReservaResponseDTO getReservaById(@PathVariable Long id) {
        return reservaService.obtenerReservaPorId(id);
    }

    // Eliminar una reserva
    @DeleteMapping("/{id}")
    public void deleteReserva(@PathVariable Long id) {
        reservaService.eliminarReserva(id);
    }
}
