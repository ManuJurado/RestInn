package RestInn.security;

import RestInn.entities.usuarios.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ejemplo: dependiendo del tipo de usuario asignamos un rol
        String role = "ROLE_" + usuario.getClass().getSimpleName().toUpperCase();
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getNombreLogin(); // o el campo que uses para login
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getActivo() != null && usuario.getActivo();
    }
}
