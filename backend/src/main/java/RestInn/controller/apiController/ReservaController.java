package RestInn.controller.apiController;

import RestInn.dto.reservasDTO.ReservaRequestDTO;
import RestInn.dto.reservasDTO.ReservaResponseDTO;
import RestInn.entities.Reserva;
import RestInn.entities.usuarios.Usuario;
import RestInn.service.ReservaService;
import RestInn.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "*") // permite peticiones desde el frontend local
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    @Autowired
    public ReservaController(ReservaService reservaService, UsuarioService usuarioService) {
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/mias")
    @PreAuthorize("isAuthenticated()")
    public List<ReservaResponseDTO> reservasDelUsuario(Authentication auth) {
        Usuario usuario = usuarioService.buscarEntidadPorNombreLogin(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return reservaService.obtenerReservasPorUsuarioId(usuario.getId());
    }

    @GetMapping("/{userName}/misReservas")
    @PreAuthorize("isAuthenticated()") //creamos metodo para filtrar Reservas segun un Cliente logueado
    public List<ReservaResponseDTO> reservasDeCliente(@PathVariable String userName,Authentication auth) {
        if(!auth.getName().equalsIgnoreCase(userName)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "no puedes ver reservas de otro usuario");
        }

        Usuario usuario = usuarioService.buscarEntidadPorNombreLogin(userName)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        return reservaService.obtenerReservasPorUsuarioId(usuario.getId());
    }


    @PostMapping//luego tendriamos que asignar la division de roles tambien para las creaciones de reservas... un cliente solo podra reservar para si mismo...
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
