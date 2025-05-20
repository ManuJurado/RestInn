package RestInn.service;

import RestInn.dto.reservasDTO.ReservaResponseDTO;
import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.repositories.UsuarioRepository;

import java.util.List;

public class UsuarioService {
    private UsuarioRepository usuarioRepository;

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
