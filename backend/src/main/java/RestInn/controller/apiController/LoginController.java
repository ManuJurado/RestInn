package RestInn.controller.apiController;

import RestInn.entities.usuarios.Usuario;
import RestInn.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final HttpSessionSecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();

    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Esencial para que Spring reconozca la sesión
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //LINEA DE REMIERDAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
            contextRepository.saveContext(SecurityContextHolder.getContext(), request, response);

            // Hace que el JSESSIONID se cree correctamente
            request.getSession(true); // fuerza la creación de sesión si no existe

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Usuario usuario = userDetails.getUsuario();

            return ResponseEntity.ok(Map.of(
                    "message", "Login exitoso",
                    "username", usuario.getNombreLogin(),
                    "rol", usuario.getClass().getSimpleName()
            ));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Credenciales inválidas"
            ));
        }
    }


}
