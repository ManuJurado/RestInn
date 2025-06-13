package RestInn.controller.apiController;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.dto.usuariosDTO.UsuarioResponseDTO;
import RestInn.entities.usuarios.PasswordResetRequest;
import RestInn.entities.usuarios.Usuario;
import RestInn.security.JwtUtil;
import RestInn.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.authManager = authManager;
        this.jwtUtil      = jwtUtil;
        this.usuarioService      = usuarioService;
    }

    //region Registro + envío de mail
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody UsuarioRequestDTO dto) {
        String code = usuarioService.iniciarRegistro(dto);
        return ResponseEntity.ok(Map.of(
                "message", "Revisa tu mail: te hemos enviado un código de verificación",
                "code",    code
        ));
    }
    //endregion

    //region Verifica el registro
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String code) {
        usuarioService.verificarRegistro(code);
        return ResponseEntity.ok(Map.of("message","Cuenta activada. Ya puedes hacer login"));
    }
    //endregion

    //region Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password())
            );

            UserDetails ud = (UserDetails) auth.getPrincipal();

            // Obtener el usuario completo desde servicio para recuperar rol
            UsuarioResponseDTO usuarioDTO = usuarioService.buscarPorNombreLogin(ud.getUsername());

            // Suponiendo que usuario.getRolEmpleado() devuelve un enum o string rol
            String rol = usuarioDTO.getRole();

            // Generar token pasando username + rol
            String token = jwtUtil.generateAccessToken(ud.getUsername(), rol);

            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("message", "Credenciales inválidas"));
        }
    }
    //endregion

    //region Refresca el token (util para revisar validez de token segun tiempo)
    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !jwtUtil.isValidRefreshToken(refreshToken, jwtUtil.extractUsername(refreshToken))) {
            throw new RuntimeException("Refresh token inválido");
        }
        String username = jwtUtil.extractUsername(refreshToken);

        // Obtener el rol del usuario
        UsuarioResponseDTO usuario = usuarioService.buscarPorNombreLogin(username);  // o como tengas para buscar usuario
        String role = usuario.getRole();

        String newAccessToken = jwtUtil.generateAccessToken(username, role);
        return Map.of("token", newAccessToken);
    }
    //endregion

    //region Paso 1 - Pedir código de recuperación
    @PostMapping("/recovery")
    public ResponseEntity<?> iniciarRecuperacion(@RequestParam String email) {
        usuarioService.enviarCodigoRecuperacion(email);             // delega
        return ResponseEntity.ok(Map.of("message",
                "Si el mail existe recibirá un código de recuperación."));
    }
    //endregion

    //region Paso 2 – validar código y devolver username
    @GetMapping("/recovery/verify")
    public ResponseEntity<Map<String,String>> verificarCodigo(@RequestParam String code) {
        Usuario u = usuarioService.validarCodigoRecuperacion(code);
        return ResponseEntity.ok(Map.of("message","Código válido","username",u.getNombreLogin()));
    }

    //endregion

    //region establecer nueva contraseña
    @PutMapping("/recovery/reset")
    public ResponseEntity<?> resetPass(@RequestBody PasswordResetRequest dto) {
        usuarioService.resetearPassword(dto);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada"));
    }
    //endregion

    //region Inicia el registro: guarda el DTO en JSON dentro de VerificationToken
    @PostMapping("/register/initiate")
    public ResponseEntity<Map<String,String>> initiateRegistration(@RequestBody UsuarioRequestDTO dto) {
        String code = usuarioService.iniciarRegistro(dto);
        return ResponseEntity.ok(Map.of(
                "message", "Revisa tu mail: te hemos enviado un código de verificación",
                "code", code
        ));
    }
    //endregion

    //region Completa el registro cuando el usuario mete el código
    @GetMapping("/register/verify")
    public ResponseEntity<Map<String,String>> completeRegistration(@RequestParam String code) {
        try {
            usuarioService.verificarRegistro(code);
            return ResponseEntity.ok(Map.of("message","Cuenta activada correctamente. Ya puedes hacer login"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
        }
    }
    //endregion

    public static record AuthRequest(String username, String password) {}
}