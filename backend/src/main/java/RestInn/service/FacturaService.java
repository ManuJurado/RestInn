package RestInn.service;

import RestInn.dto.cobranzasDTO.FacturaRequestDTO;
import RestInn.dto.cobranzasDTO.FacturaResponseDTO;
import RestInn.dto.habitacionesDTO.HabitacionResponseDTO;
import RestInn.entities.Habitacion;
import RestInn.entities.cobranzas.Consumo;
import RestInn.entities.cobranzas.Factura;
import RestInn.entities.enums.EstadoFactura;
import RestInn.entities.enums.MetodoPago;
import RestInn.exceptions.BadRequestException;
import RestInn.repositories.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                .estado(factura.getEstado())
                .haber(factura.getHaber())
                .debe(factura.getDebe())
                .build();
    }



    public FacturaResponseDTO crearFactura(FacturaRequestDTO facReqDTO) {
        Factura factura = convertirFacturaAEntidad(facReqDTO);
        if (factura)

    }

    private Factura convertirFacturaAEntidad(FacturaRequestDTO factura) {
        return Factura.builder()
                .id(factura.getId())
                .metodoPago(factura.getMetodoPago())
                .cuotas(factura.getCuotas())
                .descuento(factura.getDescuento())
                .interes(factura.getInteres())
                .estado(factura.getEstado())
                .build();
    }

}
