package RestInn.service;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.entities.usuarios.*;
import RestInn.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ----------------------------------------
    // CREAR USUARIOS SEGÚN TIPO
    // ----------------------------------------
    public UsuarioResponseDTO crearEmpleado(UsuarioRequestDTO dto) {
        Empleado empleado = new Empleado();
        mapDtoToUsuario(dto, empleado, true);
        usuarioRepository.save(empleado);
        return mapToResponse(empleado);
    }

    public UsuarioResponseDTO crearCliente(UsuarioRequestDTO dto) {
        Cliente cliente = new Cliente();
        mapDtoToUsuario(dto, cliente, true);
        usuarioRepository.save(cliente);
        return mapToResponse(cliente);
    }

    public UsuarioResponseDTO crearAdministrador(UsuarioRequestDTO dto) {
        Administrador admin = new Administrador();
        mapDtoToUsuario(dto, admin, true);
        usuarioRepository.save(admin);
        return mapToResponse(admin);
    }

    // ----------------------------------------
    // MODIFICAR USUARIO (cualquier tipo)
    // ----------------------------------------
    public UsuarioResponseDTO modificarUsuario(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        mapDtoToUsuario(dto, usuario, false);
        usuarioRepository.save(usuario);
        return mapToResponse(usuario);
    }

    // ----------------------------------------
    // BORRAR USUARIO
    // ----------------------------------------
    public void borrarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }

    // ----------------------------------------
    // VER TODOS LOS USUARIOS
    // ----------------------------------------
    public List<UsuarioResponseDTO> verUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------
    // VER SOLO EMPLEADOS
    // ----------------------------------------
    public List<UsuarioResponseDTO> verEmpleados() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u instanceof Empleado)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------
    // BUSCAR POR ID (DTO)
    // ----------------------------------------
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return mapToResponse(usuario);
    }

    // ----------------------------------------
    // BUSCAR POR NOMBRE_LOGIN (DTO)
    // ----------------------------------------
    public UsuarioResponseDTO buscarPorNombreLogin(String nombreLogin) {
        Usuario usuario = usuarioRepository.findByNombreLogin(nombreLogin)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return mapToResponse(usuario);
    }

    // ----------------------------------------
    // BUSCAR ENTIDAD POR ID (para otros servicios)
    // ----------------------------------------
    public Optional<Usuario> buscarEntidadPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // ----------------------------------------
    // BUSCAR ENTIDAD POR NOMBRE_LOGIN (para otros servicios)
    // ----------------------------------------
    public Optional<Usuario> buscarEntidadPorNombreLogin(String nombreLogin) {
        return usuarioRepository.findByNombreLogin(nombreLogin);
    }

    // ----------------------------------------
    // UTILIDAD: mapear DTO → entidad
    // ----------------------------------------
    private void mapDtoToUsuario(UsuarioRequestDTO dto, Usuario usuario, boolean esNuevo) {
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setNombreLogin(dto.getNombreLogin());
        usuario.setDni(dto.getDni());
        usuario.setPhoneNumber(dto.getPhoneNumber());
        usuario.setEmail(dto.getEmail());
        usuario.setCUIT(dto.getCUIT());
        usuario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        if (esNuevo || (dto.getPassword() != null && !dto.getPassword().isBlank())) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }

    // ----------------------------------------
    // DTO → Response
    // ----------------------------------------
    private UsuarioResponseDTO mapToResponse(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .nombreLogin(usuario.getNombreLogin())
                .dni(usuario.getDni())
                .phoneNumber(usuario.getPhoneNumber())
                .email(usuario.getEmail())
                .CUIT(usuario.getCUIT())
                .activo(usuario.getActivo())
                .build();
    }
}
