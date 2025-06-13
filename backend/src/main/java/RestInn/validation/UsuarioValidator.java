package RestInn.validation;

import RestInn.dto.usuariosDTO.UsuarioRequestDTO;
import RestInn.entities.usuarios.Usuario;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsuarioValidator implements ConstraintValidator<UsuarioValido, UsuarioRequestDTO> {

    @Override
    public boolean isValid(UsuarioRequestDTO usuario, ConstraintValidatorContext context) {
        boolean valido = true;
        context.disableDefaultConstraintViolation();

        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("El nombre es obligatorio")
                    .addPropertyNode("nombre")
                    .addConstraintViolation();
            valido = false;
        }

        if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("El apellido es obligatorio")
                    .addPropertyNode("apellido")
                    .addConstraintViolation();
            valido = false;
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("El email es obligatorio")
                    .addPropertyNode("email")
                    .addConstraintViolation();
            valido = false;
        } else if (!usuario.getEmail().matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            context.buildConstraintViolationWithTemplate("El email no tiene un formato válido")
                    .addPropertyNode("email")
                    .addConstraintViolation();
            valido = false;
        }

        if (usuario.getPassword() == null || usuario.getPassword().length() < 6) {
            context.buildConstraintViolationWithTemplate("La contraseña debe tener al menos 6 caracteres")
                    .addPropertyNode("password")
                    .addConstraintViolation();
            valido = false;
        }

        if (usuario.getNombreLogin() == null || usuario.getNombreLogin().trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("El nombre de login es obligatorio")
                    .addPropertyNode("nombreLogin")
                    .addConstraintViolation();
            valido = false;
        }

        return valido;
    }
}
