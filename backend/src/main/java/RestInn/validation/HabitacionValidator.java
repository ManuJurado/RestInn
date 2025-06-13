package RestInn.validation;

import RestInn.entities.Habitacion;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class HabitacionValidator implements ConstraintValidator<HabitacionValida, Habitacion> {

    @Override
    public boolean isValid(Habitacion habitacion, ConstraintValidatorContext context) {
        boolean valido = true;
        context.disableDefaultConstraintViolation();

        if (habitacion.getNumero() == null || habitacion.getNumero() <= 0) {
            context.buildConstraintViolationWithTemplate("El número de habitación debe ser mayor a 0")
                    .addPropertyNode("numero")
                    .addConstraintViolation();
            valido = false;
        }

        if (habitacion.getPiso() == null || habitacion.getPiso() < 0) {
            context.buildConstraintViolationWithTemplate("El piso debe ser 0 o mayor")
                    .addPropertyNode("piso")
                    .addConstraintViolation();
            valido = false;
        }

        if (habitacion.getCapacidad() == null || habitacion.getCapacidad() <= 0) {
            context.buildConstraintViolationWithTemplate("La capacidad debe ser mayor a 0")
                    .addPropertyNode("capacidad")
                    .addConstraintViolation();
            valido = false;
        }

        if (habitacion.getCantCamas() == null || habitacion.getCantCamas() <= 0) {
            context.buildConstraintViolationWithTemplate("La cantidad de camas debe ser mayor a 0")
                    .addPropertyNode("cantCamas")
                    .addConstraintViolation();
            valido = false;
        }

        if (habitacion.getPrecioNoche() == null || habitacion.getPrecioNoche().compareTo(BigDecimal.valueOf(0.01)) < 0) {
            context.buildConstraintViolationWithTemplate("El precio por noche debe ser mayor a 0")
                    .addPropertyNode("precioNoche")
                    .addConstraintViolation();
            valido = false;
        }

        if (habitacion.getEstado() == null) {
            context.buildConstraintViolationWithTemplate("El estado es obligatorio")
                    .addPropertyNode("estado")
                    .addConstraintViolation();
            valido = false;
        }

        if (habitacion.getTipo() == null) {
            context.buildConstraintViolationWithTemplate("El tipo es obligatorio")
                    .addPropertyNode("tipo")
                    .addConstraintViolation();
            valido = false;
        }

        return valido;
    }
}
