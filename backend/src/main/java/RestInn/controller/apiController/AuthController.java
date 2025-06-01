package RestInn.controller.apiController;

import RestInn.entities.usuarios.Usuario;
import RestInn.security.CustomUserDetails;
import RestInn.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
        String token = jwtUtil.generateToken(ud.getUsername());
        // 4) Devolver solo el token
        return Map.of("token", token);
    }

    public static record AuthRequest(String username, String password) {}
}