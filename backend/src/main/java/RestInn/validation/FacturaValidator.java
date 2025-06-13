package RestInn.validation;

import RestInn.entities.cobranzas.Factura;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FacturaValidator implements ConstraintValidator<FacturaValida, Factura> {

    @Override
    public boolean isValid(Factura factura, ConstraintValidatorContext context) {
        boolean valido = true;
        context.disableDefaultConstraintViolation();

        if (factura.getCliente() == null) {
            context.buildConstraintViolationWithTemplate("El cliente es obligatorio")
                    .addPropertyNode("cliente")
                    .addConstraintViolation();
            valido = false;
        }

        if (factura.getReserva() == null) {
            context.buildConstraintViolationWithTemplate("La reserva es obligatoria")
                    .addPropertyNode("reserva")
                    .addConstraintViolation();
            valido = false;
        }

        if (factura.getFechaEmision() == null) {
            context.buildConstraintViolationWithTemplate("La fecha de emisión es obligatoria")
                    .addPropertyNode("fechaEmision")
                    .addConstraintViolation();
            valido = false;
        } else if (factura.getFechaEmision().isAfter(LocalDate.now())) {
            context.buildConstraintViolationWithTemplate("La fecha de emisión no puede estar en el futuro")
                    .addPropertyNode("fechaEmision")
                    .addConstraintViolation();
            valido = false;
        }

        if (factura.getTipoFactura() == null) {
            context.buildConstraintViolationWithTemplate("El tipo de factura es obligatorio")
                    .addPropertyNode("tipoFactura")
                    .addConstraintViolation();
            valido = false;
        }

        if (factura.getEstado() == null) {
            context.buildConstraintViolationWithTemplate("El estado de factura es obligatorio")
                    .addPropertyNode("estado")
                    .addConstraintViolation();
            valido = false;
        }

        if (factura.getSubtotal() == null || factura.getSubtotal().compareTo(BigDecimal.ZERO) < 0) {
            context.buildConstraintViolationWithTemplate("El subtotal no puede ser negativo ni nulo")
                    .addPropertyNode("subtotal")
                    .addConstraintViolation();
            valido = false;
        }

        if (factura.getMetodoPago() == null) {
            context.buildConstraintViolationWithTemplate("El método de pago es obligatorio")
                    .addPropertyNode("metodoPago")
                    .addConstraintViolation();
            valido = false;
        }

        if (factura.getCuotas() == null || factura.getCuotas() < 1) {
            context.buildConstraintViolationWithTemplate("Las cuotas deben ser al menos 1")
                    .addPropertyNode("cuotas")
                    .addConstraintViolation();
            valido = false;
        }

        if (factura.getDescuento() != null && factura.getDescuento().compareTo(BigDecimal.ZERO) < 0) {
            context.buildConstraintViolationWithTemplate("El descuento no puede ser negativo")
                    .addPropertyNode("descuento")
                    .addConstraintViolation();
            valido = false;
        }

        if (factura.getInteres() != null && factura.getInteres().compareTo(BigDecimal.ZERO) < 0) {
            context.buildConstraintViolationWithTemplate("El interés no puede ser negativo")
                    .addPropertyNode("interes")
                    .addConstraintViolation();
            valido = false;
        }

        if (factura.getTotalFinal() == null || factura.getTotalFinal().compareTo(BigDecimal.ZERO) < 0) {
            context.buildConstraintViolationWithTemplate("El total final no puede ser negativo ni nulo")
                    .addPropertyNode("totalFinal")
                    .addConstraintViolation();
            valido = false;
        }

        return valido;
    }
}
