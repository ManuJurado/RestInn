package RestInn.controller.apiController;

import RestInn.entities.usuarios.Usuario;
import RestInn.security.CustomUserDetails;
import RestInn.security.JwtUtil;
import RestInn.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    private JwtService jwtService;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil      = jwtUtil;
    }


    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequest req) {
        // 1) Autenticar credenciales
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        // 2) Extraer UserDetails
        UserDetails ud = (UserDetails) auth.getPrincipal();
        // 3) Generar JWT usando el username
        String accessToken = jwtUtil.generateToken(ud.getUsername());
        String refreshToken = jwtService.generateRefreshToken(ud.getUsername());
        // 4) Devolver solo el token
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );

    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (jwtService.isValid(refreshToken)) {
            String username = jwtService.extractUsername(refreshToken);
            String newAccessToken = jwtService.generateToken(username);
            return ResponseEntity.ok(newAccessToken);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido");
    }

    public static record AuthRequest(String username, String password) {}
}