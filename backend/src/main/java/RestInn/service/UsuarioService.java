package RestInn.service;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.entities.VerificationToken;
import RestInn.entities.enums.RolEmpleado;
import RestInn.entities.enums.TokenType;
import RestInn.entities.usuarios.*;
import RestInn.repositories.UsuarioRepository;
import RestInn.repositories.VerificationTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {


    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenRepository tokenRepo;
    private final EmailService emailService;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EmailService emailService, VerificationTokenRepository tokenRepo) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenRepo = tokenRepo;
    }

    //region CREAR USUARIOS SEGÚN TIPO
    public UsuarioResponseDTO crearEmpleado(UsuarioRequestDTO dto) {
        validarUnicidad(dto, null);

        Empleado empleado = new Empleado();
        // Asigna el rol específico
        empleado.setRolEmpleado(dto.getRolEmpleado());

        mapDtoToUsuario(dto, empleado, true);
        usuarioRepository.save(empleado);
        return mapToResponse(empleado);
    }

    public UsuarioResponseDTO crearCliente(UsuarioRequestDTO dto) {
        validarUnicidad(dto, null);
        Cliente cliente = new Cliente();
        mapDtoToUsuario(dto, cliente, true);
        usuarioRepository.save(cliente);
        return mapToResponse(cliente);
    }

    public UsuarioResponseDTO crearAdministrador(UsuarioRequestDTO dto) {
        validarUnicidad(dto, null);
        Administrador admin = new Administrador();
        mapDtoToUsuario(dto, admin, true);
        usuarioRepository.save(admin);
        return mapToResponse(admin);
    }
    //endregion

    //region MODIFICAR USUARIO (cualquier tipo)
    @Transactional
    public UsuarioResponseDTO modificarUsuario(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        validarUnicidad(dto, id);

        // —— Validar cambio de contraseña si se pidió ——
        if (dto.getOldPassword() != null && !dto.getOldPassword().isBlank()) {
            // debe venir también dto.password (nueva)
            if (dto.getPassword() == null || dto.getPassword().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar la nueva contraseña");
            }
            // validar que oldPassword coincida
            if (!passwordEncoder.matches(dto.getOldPassword(), usuario.getPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
            }
            // todo OK → seteamos la nueva
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // mapeo de otros campos
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setPhoneNumber(dto.getPhoneNumber());
        usuario.setCuit(dto.getCuit());

        usuarioRepository.save(usuario);
        return mapToResponse(usuario);
    }
    //endregion

    //region BORRAR USUARIO
    public void borrarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }
    //endregion

    //region VER TODOS LOS USUARIOS
    public List<UsuarioResponseDTO> verUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    //endregion

    //region VER SOLO EMPLEADOS
    public List<UsuarioResponseDTO> verEmpleados() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u instanceof Empleado)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    //endregion

    //region VER SOLO CLIENTES
    public List<UsuarioResponseDTO> verClientes() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u instanceof Cliente)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    //endregion

    //region BUSCAR POR ID (DTO)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return mapToResponse(usuario);
    }
    //endregion

    //region BUSCAR POR NOMBRE_LOGIN (DTO)
    public UsuarioResponseDTO buscarPorNombreLogin(String nombreLogin) {
        Usuario usuario = usuarioRepository.findByNombreLogin(nombreLogin)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return mapToResponse(usuario);
    }
    //endregion

    //region BUSCAR ENTIDAD POR ID (para otros servicios)
    public Optional<Usuario> buscarEntidadPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    //endregion

    //region BUSCAR ENTIDAD POR NOMBRE_LOGIN (para otros servicios)
    public Optional<Usuario> buscarEntidadPorNombreLogin(String nombreLogin) {
        return usuarioRepository.findByNombreLogin(nombreLogin);
    }
    //endregion

    //region UTILIDAD: Validar unicidad de nombreLogin y email
    private void validarUnicidad(UsuarioRequestDTO dto, Long idExistente) {
        Optional<Usuario> existentePorLogin = usuarioRepository.findByNombreLogin(dto.getNombreLogin());
        if (existentePorLogin.isPresent() && !existentePorLogin.get().getId().equals(idExistente)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya está en uso.");
        }

        Optional<Usuario> existentePorEmail = Optional.ofNullable(usuarioRepository.findByEmail(dto.getEmail()));
        if (existentePorEmail.isPresent() && !existentePorEmail.get().getId().equals(idExistente)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado.");
        }
    }
    //endregion

    //region DTO → entidad
    private void mapDtoToUsuario(UsuarioRequestDTO dto, Usuario usuario, boolean esNuevo) {
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setNombreLogin(dto.getNombreLogin());
        usuario.setDni(dto.getDni());
        usuario.setPhoneNumber(dto.getPhoneNumber());
        usuario.setEmail(dto.getEmail());
        usuario.setCuit(dto.getCuit());
        usuario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        if (esNuevo || (dto.getPassword() != null && !dto.getPassword().isBlank())) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }
    //endregion

    //region DTO → Response
    public UsuarioResponseDTO mapToResponse(Usuario usuario) {
        UsuarioResponseDTO.UsuarioResponseDTOBuilder builder = UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .nombreLogin(usuario.getNombreLogin())
                .dni(usuario.getDni())
                .phoneNumber(usuario.getPhoneNumber())
                .email(usuario.getEmail())
                .cuit(usuario.getCuit())
                .activo(usuario.getActivo());

        if (usuario instanceof Empleado) {
            // Para empleados, usamos el enum RolEmpleado
            String rolEmp = ((Empleado) usuario).getRolEmpleado().name();
            builder.role(rolEmp);
        } else {
            // Para cliente o admin, el metodo getRole() ya devuelve CLIENTE o ADMINISTRADOR
            builder.role(usuario.getRole());
        }

        return builder.build();
    }
    //endregion

    //region Enviar código de verificacion
    @Transactional
    public void enviarCodigoRecuperacion(String email) {
        Usuario u = Optional.ofNullable(usuarioRepository.findByEmail(email))
                .filter(Usuario::getActivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un usuario activo con ese correo"));

        tokenRepo.deleteByUsuarioAndType(u, TokenType.PASSWORD_RESET);

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        VerificationToken t = new VerificationToken();
        t.setCode(code);
        t.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        t.setType(TokenType.PASSWORD_RESET);
        t.setUsuario(u);
        tokenRepo.save(t);

        emailService.sendVerificationHtml(
                u.getEmail(),
                "Código de recuperación RestInn",
                "Tu código es: " + code
        );
    }

    //endregion

    //region Verificar código (si querés un paso previo)
    @Transactional
    public Usuario validarCodigoRecuperacion(String code) {
        VerificationToken t = tokenRepo
                .findByCodeAndType(code, TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Código inválido"));
        if (t.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Código expirado");
        return t.getUsuario();
    }

    //endregion

    //region Resetear contraseña
    @Transactional
    public void resetearPassword(PasswordResetRequest dto) {
        VerificationToken t = tokenRepo
                .findByCodeAndType(dto.getCode(), TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Código inválido"));
        if (t.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Código expirado");

        Usuario u = t.getUsuario();
        u.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuarioRepository.save(u);
        tokenRepo.delete(t);
    }
    //endregion

    //region Inicia el registro de un usuario
    @Transactional
    public String iniciarRegistro(UsuarioRequestDTO dto) {
        // 1) unicidad…
        if (usuarioRepository.existsByEmail(dto.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya en uso");
        if (usuarioRepository.existsByNombreLogin(dto.getNombreLogin()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login ya en uso");

        // 2) generar código
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        // 3) serializar DTO
        String json;
        try {
            json = new ObjectMapper().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno");
        }

        // 4) guardar token REGISTRATION
        VerificationToken t = new VerificationToken();
        t.setCode(code);
        t.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        t.setType(TokenType.REGISTRATION);
        t.setUserDtoJson(json);
        tokenRepo.save(t);

        // 5) enviar email con el código…
        String link = "http://restinn.sytes.net/clientes/verificar.html?code=" + code;
        emailService.sendVerificationHtml(
                dto.getEmail(),
                "Verifica tu cuenta RestInn",
                "Tu código es: " + code + "\nO haz clic: " + link
        );

        return code;
    }
    //endregion

    //region Verificar el registro
    @Transactional
    public void verificarRegistro(String code) {
        VerificationToken t = tokenRepo
                .findByCodeAndType(code, TokenType.REGISTRATION)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido"));

        if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(t);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código expirado");
        }

        // deserializar DTO
        UsuarioRequestDTO dto;
        try {
            dto = new ObjectMapper().readValue(t.getUserDtoJson(), UsuarioRequestDTO.class);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno");
        }

        // crear el Cliente definitivo
        Cliente c = new Cliente();
        mapDtoToUsuario(dto, c, true);
        c.setActivo(true);
        usuarioRepository.save(c);

        // borrar el token
        tokenRepo.delete(t);
    }
    //endregion

}
