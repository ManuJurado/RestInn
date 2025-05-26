package RestInn.service;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.entities.usuarios.*;
import RestInn.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    // CREAR USUARIOS SEGÚN TIPO
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

    public UsuarioResponseDTO crearAdministrador(UsuarioRequestDTO dto) {// ver si dejar o sacar
        Administrador admin = new Administrador();
        mapDtoToUsuario(dto, admin, true);
        usuarioRepository.save(admin);
        return mapToResponse(admin);
    }

    // MODIFICAR USUARIO (cualquier tipo)
    public UsuarioResponseDTO modificarUsuario(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        mapDtoToUsuario(dto, usuario, false);
        usuarioRepository.save(usuario);
        return mapToResponse(usuario);
    }

    // BORRAR USUARIO
    public void borrarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }

    // VER TODOS
    public List<UsuarioResponseDTO> verUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapToResponse(usuario);
    }

    public UsuarioResponseDTO buscarPorNombreLogin(String nombreLogin) {
        Usuario usuario = usuarioRepository.findByNombreLogin(nombreLogin)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapToResponse(usuario);
    }

    // UTILIDAD: mapear DTO → entidad
    private void mapDtoToUsuario(UsuarioRequestDTO dto, Usuario usuario, boolean esNuevo) {// metodo encargado de mapear un usuario recibido al tipo que es
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

    // OPCIONAL: si querés centralizar creación por tipo (sin pasarlo en el DTO)
    private Usuario crearInstanciaPorTipo(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "empleado" -> new Empleado();
            case "cliente" -> new Cliente();
            case "administrador" -> new Administrador();
            default -> throw new IllegalArgumentException("Tipo inválido");
        };
    }

    // DTO → Response
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
