package RestInn.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FacturaValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FacturaValida {
    String message() default "Factura no válida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
