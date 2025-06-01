package RestInn.service;

import RestInn.dto.cobranzasDTO.FacturaRequestDTO;
import RestInn.dto.cobranzasDTO.FacturaResponseDTO;
import RestInn.entities.Reserva;
import RestInn.entities.cobranzas.Consumo;
import RestInn.entities.cobranzas.Factura;
import RestInn.entities.enums.MetodoPago;
import RestInn.entities.enums.TipoFactura;
import RestInn.entities.usuarios.Cliente;
import RestInn.exceptions.BadRequestException;
import RestInn.repositories.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {
    @Autowired
    private  FacturaRepository facturaRepository;
    @Autowired
    private ReservaService reservaService;

    public List<FacturaResponseDTO> listarTodas() {
        return facturaRepository.findAll()
                .stream()
                .map(this::convertirFacturaAResponseDTO)
                .toList();
    }

    public List<FacturaResponseDTO> listarPorReserva(Long reservaId) {
        return facturaRepository.findAll()
                .stream()
                .filter(f -> f.getReserva().getId().equals(reservaId))
                .map(this::convertirFacturaAResponseDTO)
                .toList();
    }

    public Optional<Factura> buscarEntidadFacturaPorId(Long id) {
         return facturaRepository.findById(id);
    }

    public Optional<FacturaResponseDTO> buscarFacturaDTOPorId(Long id) {
        Optional<Factura> factura = facturaRepository.findById(id);
        if(factura.isPresent()) {
            return Optional.ofNullable(convertirFacturaAResponseDTO(factura.get()));
        } else {
            throw new BadRequestException("El id de la factura no existe.");
        }
    }

    private FacturaResponseDTO convertirFacturaAResponseDTO(Factura factura) {
        return FacturaResponseDTO.builder()
                .id(factura.getId())
                .fechaEmision(factura.getFechaEmision())
                .consumos(factura.getConsumos())
                .subtotal(factura.getSubtotal())
                .metodoPago(factura.getMetodoPago())
                .cuotas(factura.getCuotas())
                .descuento(factura.getDescuento())
                .interes(factura.getInteres())
                .totalFinal(factura.getTotalFinal())
                .build();
    }


    // Crear factura que se genera al realizar la reserva
    public FacturaResponseDTO crearFacturaReserva(Reserva reserva) {
        Factura factura = new Factura();
        factura.setCliente((Cliente) reserva.getUsuario());
        factura.setReserva(reserva);
        factura.setFechaEmision(LocalDate.now());
        factura.setTipoFactura(TipoFactura.RESERVA);
        factura.setConsumos(null);
        factura.setSubtotal(calcularSubtotalReserva(reserva));
        factura.setMetodoPago(null);
        factura.setCuotas(null);
        factura.setDescuento(null);
        factura.setInteres(null);
        factura.setTotalFinal(calcularTotalReserva(factura));
        return convertirFacturaAResponseDTO(factura);
    }

    // Crear factura de los consumos que se generara en el checkout
    public FacturaResponseDTO crearFacturaConsumos(FacturaRequestDTO facReqDTO, Reserva reserva, List<Consumo> consumos) {
        Factura factura = new Factura();
        factura.setCliente((Cliente) reserva.getUsuario());
        factura.setReserva(reserva);
        factura.setFechaEmision(LocalDate.now());
        factura.setTipoFactura(TipoFactura.CONSUMOS);
        factura.setConsumos(consumos);
        factura.setSubtotal(BigDecimal.ZERO);
        factura.setMetodoPago(null);
        factura.setCuotas(null);
        factura.setDescuento(null);
        factura.setInteres(null);
        factura.setTotalFinal(calcularTotalConsumos(factura));
        return convertirFacturaAResponseDTO(factura);
    }

    private BigDecimal calcularSubtotalReserva(Reserva reserva) {
        Integer cantidadDias = (int) ChronoUnit.DAYS.between(reserva.getFechaIngreso(), reserva.getFechaSalida());
        BigDecimal precioHabitacion = reserva.getHabitacion().getPrecioNoche();
        return precioHabitacion.multiply(BigDecimal.valueOf(cantidadDias));
    }

    private BigDecimal calcularTotalReserva(Factura factura) {
        BigDecimal subtotal = factura.getSubtotal();
        BigDecimal total = subtotal;

        if (factura.getMetodoPago() == MetodoPago.EFECTIVO) {
            BigDecimal descuento = BigDecimal.valueOf(10); // 10%
            factura.setDescuento(descuento);
            total = subtotal.subtract(subtotal.multiply(descuento).divide(BigDecimal.valueOf(100)));
        } else if (factura.getMetodoPago() == MetodoPago.TARJETA_CREDITO) {
            Integer cuotas = factura.getCuotas();
            BigDecimal interes = calcularInteresPorCuotas(cuotas);
            factura.setInteres(interes);
            total = subtotal.add(subtotal.multiply(interes).divide(BigDecimal.valueOf(100)));
        }
        factura.setTotalFinal(total);
        return total;
    }


    private BigDecimal calcularInteresPorCuotas(Integer cuotas) {
        if (cuotas == null || cuotas <= 1) {
            return BigDecimal.ZERO;
        } else if (cuotas <= 3) {
            return BigDecimal.valueOf(10);
        } else if (cuotas <= 6) {
            return BigDecimal.valueOf(15);
        } else {
            return BigDecimal.valueOf(20);
        }
    }

    private Factura convertirFacturaAEntidad(FacturaRequestDTO factura) {
        return Factura.builder()
                .id(factura.getId())
                .metodoPago(factura.getMetodoPago())
                .cuotas(factura.getCuotas())
                .descuento(factura.getDescuento())
                .interes(factura.getInteres())
                .build();
    }

}
