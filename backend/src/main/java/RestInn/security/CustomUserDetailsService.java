package RestInn.security;

import RestInn.entities.usuarios.Administrador;
import RestInn.entities.usuarios.Cliente;
import RestInn.entities.usuarios.Empleado;
import RestInn.entities.usuarios.Usuario;
import RestInn.repositories.UsuarioRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nombreLogin) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreLogin(nombreLogin)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Determinar rol real (con prefijo ROLE_)
        String rol;
        if (usuario instanceof Administrador) {
            rol = "ROLE_ADMINISTRADOR";
        } else if (usuario instanceof Empleado empleado) {
            rol = "ROLE_" + empleado.getRolEmpleado().name(); // por ejemplo, ROLE_LIMPIEZA
        } else if (usuario instanceof Cliente) {
            rol = "ROLE_CLIENTE";
        } else {
            throw new UsernameNotFoundException("Tipo de usuario desconocido");
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(rol));

        return new CustomUserDetails(usuario);

    }

}