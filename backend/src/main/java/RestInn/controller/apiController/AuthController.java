package RestInn.controller.apiController;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
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

    // — Registro + envío de mail
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody UsuarioRequestDTO dto) {
        // esto ahora crea el usuario y genera el token internamente:
        String code = usuarioService.registrarClienteConVerificacion(dto);
        return ResponseEntity.ok(Map.of(
                "message", "Revisa tu mail: te hemos enviado un código de verificación",
                "code",    code
        ));
    }

    // — Verificación de cuenta
    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(@RequestParam String code) {
        try {
            usuarioService.verifyAccount(code);
            return ResponseEntity.ok(Map.of(
                    "message", "Cuenta activada correctamente. Ya puedes hacer login"
            ));
        } catch (ResponseStatusException e) {
            // responde con el status que el servicio indicó (400 Bad Request) y su mensaje
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
        }
    }

    // — Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(),req.password())
            );
            String token = jwtUtil.generateAccessToken(((UserDetails)auth.getPrincipal()).getUsername());
            return ResponseEntity.ok(Map.of("token",token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("message","Credenciales inválidas"));
        }
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !jwtUtil.isValid(refreshToken, jwtUtil.extractUsername(refreshToken))) {
            throw new RuntimeException("Refresh token inválido");
        }
        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);
        return Map.of("token", newAccessToken);
    }



    /** Paso 1 – pedir código de recuperación */
    @PostMapping("/recovery")
    public ResponseEntity<?> iniciarRecuperacion(@RequestParam String email) {
        usuarioService.enviarCodigoRecuperacion(email);             // delega
        return ResponseEntity.ok(Map.of("message",
                "Si el mail existe recibirá un código de recuperación."));
    }

    /** Paso 2 – validar código y devolver username */
    @GetMapping("/recovery/verify")
    public ResponseEntity<Map<String, String>> verificarCodigo(@RequestParam String code) {
        // este metodo lanzará 400 si no existe o expiró
        Usuario usuario = usuarioService.validarCodigoRecuperacion(code);
        return ResponseEntity.ok(Map.of(
                "message",  "Código válido",
                "username", usuario.getNombreLogin()
        ));
    }


    /** Paso 3 – establecer nueva contraseña */
    @PutMapping("/recovery/reset")
    public ResponseEntity<?> resetPass(@RequestBody PasswordResetRequest dto) {
        usuarioService.resetearPassword(dto);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada"));
    }

    public static record AuthRequest(String username, String password) {}
}