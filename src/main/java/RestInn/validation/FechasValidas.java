package RestInn.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FechaValidaValidator.class)
@Target(ElementType.TYPE) // <- Esto permite usar la anotación sobre clases
@Retention(RetentionPolicy.RUNTIME)
public @interface FechasValidas {
    String message() default "La fecha de ingreso debe ser anterior a la fecha de salida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
