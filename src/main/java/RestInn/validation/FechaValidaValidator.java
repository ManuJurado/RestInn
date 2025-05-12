package RestInn.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import RestInn.dto.reservasDTO.ReservaRequestDTO;

// Validación para la anotación FechasValidas
public class FechaValidaValidator implements ConstraintValidator<FechasValidas, ReservaRequestDTO> {

    @Override
    public void initialize(FechasValidas constraintAnnotation) {
        // Aquí podrías hacer alguna inicialización si es necesario
    }

    @Override
    public boolean isValid(ReservaRequestDTO dto, ConstraintValidatorContext context) {
        // Si alguna de las fechas es nula, consideramos que la validación falla
        if (dto.getFechaIngreso() == null || dto.getFechaSalida() == null) {
            return false;
        }

        // Comprobamos que la fecha de ingreso no sea posterior a la de salida
        return !dto.getFechaIngreso().isAfter(dto.getFechaSalida());
    }
}
