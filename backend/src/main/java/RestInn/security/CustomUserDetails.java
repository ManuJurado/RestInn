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
        // 1) Caso Empleado: usamos el enum RolEmpleado que trae la instancia
        if (usuario instanceof Empleado) {
            Empleado emp = (Empleado) usuario;
            RolEmpleado rolEnum = emp.getRolEmpleado();
            // Ejemplos de RolEmpleado: RECEPCIONISTA, LIMPIEZA, CONSERJE, etc.
            String rolTxt = "ROLE_" + rolEnum.name().toUpperCase();
            return List.of(new SimpleGrantedAuthority(rolTxt));
        }

        // 2) Caso Cliente: usamos la clase Cliente -> "ROLE_CLIENTE"
        if (usuario instanceof Cliente) {
            return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
        }

        // 3) Caso Administrador: usamos la clase Administrador -> "ROLE_ADMINISTRADOR"
        if (usuario instanceof Administrador) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
        }

        // 4) Fallback: si llega algún otro tipo (por si agregas más subclases en el futuro):
        String rolPorClase = "ROLE_" + usuario.getClass().getSimpleName().toUpperCase();
        return List.of(new SimpleGrantedAuthority(rolPorClase));
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
