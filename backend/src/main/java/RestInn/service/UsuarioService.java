package RestInn.service;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.entities.VerificationToken;
import RestInn.entities.enums.RolEmpleado;
import RestInn.entities.usuarios.*;
import RestInn.repositories.UsuarioRepository;
import RestInn.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    // ----------------------------------------
    // CREAR USUARIOS SEGÚN TIPO
    // ----------------------------------------
    public UsuarioResponseDTO crearEmpleado(UsuarioRequestDTO dto) {
        validarUnicidad(dto, null);
        Empleado empleado = new Empleado();
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

    // ----------------------------------------
    // MODIFICAR USUARIO (cualquier tipo)
    // ----------------------------------------
    public UsuarioResponseDTO modificarUsuario(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        validarUnicidad(dto, id);

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
    // UTILIDAD: Validar unicidad de nombreLogin y email
    // ----------------------------------------
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
        String roleValue;

        if (usuario instanceof Empleado) {
            roleValue = ((Empleado) usuario).getRolEmpleado().name();
        } else if (usuario instanceof Cliente) {
            roleValue = "CLIENTE";
        } else if (usuario instanceof Administrador) {
            roleValue = "ADMINISTRADOR";
        } else {
            roleValue = usuario.getClass().getSimpleName().toUpperCase();
        }

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
                .role(roleValue)
                .build();
    }

    @Transactional
    public String registrarClienteConVerificacion(UsuarioRequestDTO dto) {
        // 1) validar unicidad estricta de nombreLogin (sólo exactos)
        Optional<Usuario> porLogin = usuarioRepository.findByNombreLogin(dto.getNombreLogin());
        if (porLogin.isPresent() && Boolean.TRUE.equals(porLogin.get().getActivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya está en uso.");
        }

        // 2) Buscar o crear cliente inactivo
        Usuario cliente = usuarioRepository.findByEmail(dto.getEmail());
        if (cliente != null) {
            if (Boolean.TRUE.equals(cliente.getActivo())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado y activo.");
            }
            mapDtoToUsuario(dto, cliente, false);
            usuarioRepository.save(cliente);
        } else {
            validarUnicidad(dto, null);
            cliente = new Cliente();
            mapDtoToUsuario(dto, cliente, true);
            cliente.setActivo(false);
            usuarioRepository.save(cliente);
        }

        // 3) borrar cualquier token viejo para este usuario
        tokenRepo.deleteByUsuario(cliente);

        // 4) generar y guardar nuevo token
        int codeInt = new SecureRandom().nextInt(900_000) + 100_000; // 100000..999999
        String code = String.valueOf(codeInt);

        VerificationToken t = new VerificationToken();
        t.setCode(code);
        t.setExpiresAt(LocalDateTime.now().plusMinutes(3));
        t.setUsuario(cliente);
        tokenRepo.save(t);

        // 5) enviar email con código destacado
        String link = "http://restinn.sytes.net/clientes/verificar.html?code=" + code;
        String body = """
        ¡Bienvenido a RestInn!

        Tu código de verificación es: \n

       %s\n

        (cópialo y pégalo en la página de verificación)

        O bien haz clic aquí para verificar automáticamente:
        %s

        Este código expira en 3 minutos.
        """.formatted(code, link);
        emailService.sendVerificationHtml(
                cliente.getEmail(),
                "Verifica tu cuenta RestInn",
                body
        );

        return code;
    }

    public void verifyAccount(String code) {
        tokenRepo.findByCode(code).ifPresentOrElse(t -> {
            if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expirado");
            }
            Usuario u = t.getUsuario();
            u.setActivo(true);
            usuarioRepository.save(u);
            tokenRepo.delete(t);
        }, () -> {
            // <— AÑADE ESTE LOG PARA DEPURAR
            System.out.println("Tokens en BD:");
            tokenRepo.findAll().forEach(tt ->
                    System.out.printf("  %s (expira %s)%n", tt.getCode(), tt.getExpiresAt())
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido");
        });
    }

    /* --- 1) Enviar código --- */
    @Transactional
    public void enviarCodigoRecuperacion(String email) {
        Usuario u = Optional.ofNullable(usuarioRepository.findByEmail(email))
                .filter(Usuario::getActivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un usuario activo con ese correo"));

        // 1) borrar token viejo
        tokenRepo.deleteByUsuario(u);
        tokenRepo.flush();

        // 2) generar nuevo token de 6 dígitos
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        VerificationToken t = new VerificationToken();
        t.setCode(code);
        t.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        t.setUsuario(u);
        tokenRepo.save(t);

        // 3) construir email incluyendo el nombre de usuario
        String username = u.getNombreLogin();
        String link = "http://restinn.sytes.net/clientes/recuperar-password.html";
        String body = """
        ¡Hola %s!

        Hemos recibido una solicitud para restablecer la contraseña de tu cuenta.

        Tu código de recuperación es:

        <h2 style="margin:12px 0;font-size:32px;">%s</h2>

        Si lo prefieres, haz clic en el siguiente enlace e ingresa tu código allí:
        <a href="%s">Restablecer contraseña</a>

        Este código expirará en 10 minutos.

        Si no solicitaste este correo, puedes ignorarlo.
        """
                .formatted(username, code, link);

        emailService.sendVerificationHtml(
                u.getEmail(),
                "Restablecimiento de contraseña – RestInn",
                body
        );
    }


    /* --- 2) Verificar código (si querés un paso previo) --- */
    @Transactional
    public Usuario validarCodigoRecuperacion(String code) {
        VerificationToken t = tokenRepo.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido"));
        if (t.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código expirado");
        // no activamos aquí, solo devolvemos el usuario
        return t.getUsuario();
    }


    /* --- 3) Resetear contraseña --- */
    @Transactional
    public void resetearPassword(PasswordResetRequest dto) {
        VerificationToken t = tokenRepo.findByCode(dto.getCode())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Código inválido"));
        if (t.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código expirado");

        Usuario u = t.getUsuario();
        u.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuarioRepository.save(u);
        tokenRepo.delete(t);                   // token consumido
    }

}
