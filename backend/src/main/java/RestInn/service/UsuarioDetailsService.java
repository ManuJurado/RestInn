package RestInn.service;

import RestInn.entities.usuarios.Usuario;
import RestInn.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nombreLogin) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreLogin(nombreLogin)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + nombreLogin));

        String rol;
        if (usuario.getClass().getSimpleName().equals("Cliente")) rol = "ROLE_CLIENTE";
        else if (usuario.getClass().getSimpleName().equals("Administrador")) rol = "ROLE_ADMINISTRADOR";
        else if (usuario.getClass().getSimpleName().equals("Empleado")) rol = "ROLE_EMPLEADO";
        else rol = "ROLE_USER";

        List<SimpleGrantedAuthority> roles = List.of(new SimpleGrantedAuthority(rol));

        return new User(
                usuario.getNombreLogin(),
                usuario.getPassword(),  // importante: debe estar encriptada con BCrypt
                usuario.getActivo(),    // enabled
                true,                   // accountNonExpired
                true,                   // credentialsNonExpired
                true,                   // accountNonLocked
                roles
        );
    }
}
