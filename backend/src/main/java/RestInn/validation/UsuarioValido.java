package RestInn.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsuarioValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsuarioValido {
    String message() default "Usuario no válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
