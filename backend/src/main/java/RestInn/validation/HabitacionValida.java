package RestInn.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HabitacionValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HabitacionValida {
    String message() default "Habitación no válida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
