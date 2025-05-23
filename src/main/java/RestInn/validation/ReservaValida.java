package RestInn.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReservaValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReservaValida {
    String message() default "Reserva no válida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
