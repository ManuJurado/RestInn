package RestInn.validation;

import RestInn.dto.reservasDTO.ReservaRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReservaValidator implements ConstraintValidator<ReservaValida, ReservaRequestDTO> {

    @Override
    public boolean isValid(ReservaRequestDTO dto, ConstraintValidatorContext context) {
        boolean valido = true;
        context.disableDefaultConstraintViolation();

        LocalDate ingreso = dto.getFechaIngreso();
        LocalDate salida = dto.getFechaSalida();

        if (ingreso == null || salida == null) {
            context.buildConstraintViolationWithTemplate("Las fechas no pueden ser nulas")
                    .addConstraintViolation();
            return false;
        }

        if (ingreso.isAfter(salida)) {
            context.buildConstraintViolationWithTemplate("La fecha de ingreso debe ser anterior a la de salida")
                    .addPropertyNode("fechaIngreso")
                    .addConstraintViolation();
            valido = false;
        }

        if (ingreso.isBefore(LocalDate.now())) {
            context.buildConstraintViolationWithTemplate("La fecha de ingreso no puede estar en el pasado")
                    .addPropertyNode("fechaIngreso")
                    .addConstraintViolation();
            valido = false;
        }

        if (dto.getHabitacionId() == null) {
            context.buildConstraintViolationWithTemplate("El ID de habitación es obligatorio")
                    .addPropertyNode("habitacionId")
                    .addConstraintViolation();
            valido = false;
        }

        if (dto.getHuespedes() == null || dto.getHuespedes().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Debe haber al menos un huésped en la reserva")
                    .addPropertyNode("huespedes")
                    .addConstraintViolation();
            valido = false;
        }

        return valido;
    }
}
