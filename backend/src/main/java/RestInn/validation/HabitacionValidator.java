package RestInn.validation;

import RestInn.dto.habitacionesDTO.HabitacionRequestDTO;
import RestInn.entities.Habitacion;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class HabitacionValidator implements ConstraintValidator<HabitacionValida, HabitacionRequestDTO> {

    @Override
    public boolean isValid(HabitacionRequestDTO dto, ConstraintValidatorContext context) {
        boolean valido = true;
        context.disableDefaultConstraintViolation();

        if (dto.getNumero() == null || dto.getNumero() <= 0) {
            context.buildConstraintViolationWithTemplate("El número de habitación debe ser mayor a 0")
                    .addPropertyNode("numero")
                    .addConstraintViolation();
            valido = false;
        }

        if (dto.getPiso() == null || dto.getPiso() < 1 || dto.getPiso() > 4) {
            context.buildConstraintViolationWithTemplate("El piso debe estar entre 1 y 4")
                    .addPropertyNode("piso")
                    .addConstraintViolation();
            valido = false;
        }

        if (dto.getCapacidad() == null || dto.getCapacidad() <= 0 || dto.getCapacidad() > 5) {
            context.buildConstraintViolationWithTemplate("La capacidad debe estar entre 1 y 5")
                    .addPropertyNode("capacidad")
                    .addConstraintViolation();
            valido = false;
        }

        if (dto.getCantCamas() == null || dto.getCantCamas() <= 0 || dto.getCantCamas() > 4) {
            context.buildConstraintViolationWithTemplate("La cantidad de camas debe estar entre 1 y 4")
                    .addPropertyNode("cantCamas")
                    .addConstraintViolation();
            valido = false;
        }

        if (dto.getPrecioNoche() == null || dto.getPrecioNoche().compareTo(BigDecimal.valueOf(0.01)) < 0) {
            context.buildConstraintViolationWithTemplate("El precio por noche debe ser mayor a 0")
                    .addPropertyNode("precioNoche")
                    .addConstraintViolation();
            valido = false;
        }

        if (dto.getEstado() == null) {
            context.buildConstraintViolationWithTemplate("El estado es obligatorio")
                    .addPropertyNode("estado")
                    .addConstraintViolation();
            valido = false;
        }

        if (dto.getTipo() == null) {
            context.buildConstraintViolationWithTemplate("El tipo es obligatorio")
                    .addPropertyNode("tipo")
                    .addConstraintViolation();
            valido = false;
        }

        return valido;
    }
}

