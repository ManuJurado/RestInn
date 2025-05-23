package RestInn.service;

import RestInn.dto.reservasDTO.ReservaResponseDTO;
import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    // Constructor para inyección por constructor (recomendado)
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO crearUsuarioEmpleado(UsuarioRequestDTO usuReqDTO){
        return null;
    }

    public UsuarioResponseDTO modificarUsuarioEmpleado(Long id, UsuarioRequestDTO usuReqDTO){
        return null;
    }

    public void borrarUsuarioEmpleado(Long id){
    }

    public List<UsuarioResponseDTO> verUsuarios(){
        return null;
    }

    public UsuarioResponseDTO modificarCuenta(Long id, UsuarioRequestDTO usuReqDTO){
        return null;
    }

    public void borrarCuenta(Long id) {
    }

    public List<ReservaResponseDTO> verHistorialReservasPropias(Long idUsuario){
        return null;
    }
}
