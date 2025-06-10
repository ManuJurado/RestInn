package RestInn.security;

import RestInn.entities.enums.RolEmpleado;
import RestInn.entities.usuarios.Administrador;
import RestInn.entities.usuarios.Cliente;
import RestInn.entities.usuarios.Empleado;
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
        String roleString;

        if (usuario instanceof Empleado) {
            // Si es empleado, tomamos su RolEmpleado
            RolEmpleado rolEmp = ((Empleado) usuario).getRolEmpleado();
            roleString = "ROLE_" + rolEmp.name();   // e.g. "ROLE_LIMPIEZA"
        }
        else if (usuario instanceof Cliente) {
            roleString = "ROLE_CLIENTE";
        }
        else if (usuario instanceof Administrador) {
            roleString = "ROLE_ADMINISTRADOR";
        }
        else {
            // Fallback: por si hubiera otra subclase en el futuro
            roleString = "ROLE_" + usuario.getClass().getSimpleName().toUpperCase();
        }

        return List.of(new SimpleGrantedAuthority(roleString));
    }

    public static CustomUserDetails fromUsuario(Usuario usuario) {
        return new CustomUserDetails(usuario);
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