package RestInn.service;

import RestInn.dto.cobranzasDTO.ConsumoRequestDTO;
import RestInn.dto.cobranzasDTO.ConsumoResponseDTO;
import RestInn.entities.cobranzas.Consumo;
import RestInn.entities.cobranzas.Factura;
import RestInn.entities.enums.EstadoFactura;
import RestInn.entities.enums.TipoFactura;
import RestInn.repositories.ConsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ConsumoService {
    @Autowired
    private ConsumoRepository consumoRepository;
    @Autowired
    private FacturaService facturaService;

    //region Mapear un ConsumoRequestDTO a una entidad Consumo.
    public Consumo mapearDesdeRequestDTO(ConsumoRequestDTO dto, Factura factura) {
        BigDecimal subtotal = dto.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.getCantidad()));

        return Consumo.builder()
                .descripcion(dto.getDescripcion())
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .subtotal(subtotal)
                .factura(factura)
                .build();
    }
    //endregion

    //region Mapear una entidad Consumo a un ConsumoResponseDTO.
    public ConsumoResponseDTO mapearAResponseDTO(Consumo consumo) {
        return ConsumoResponseDTO.builder()
                .id(consumo.getId())
                .descripcion(consumo.getDescripcion())
                .cantidad(consumo.getCantidad())
                .precioUnitario(consumo.getPrecioUnitario())
                .subtotal(consumo.getSubtotal())
                .build();
    }
    //endregion

    //region Crear un nuevo consumo y asociarlo a una factura EN_PROCESO.
    public ConsumoResponseDTO crearConsumo(Long facturaId, ConsumoRequestDTO dto) {
        Factura factura = facturaService.obtenerFacturaPorId(facturaId);

        if (!factura.getTipoFactura().equals(TipoFactura.CONSUMOS) ||
                !factura.getEstado().equals(EstadoFactura.EN_PROCESO)) {
            throw new IllegalStateException("Solo se pueden agregar consumos a facturas EN_PROCESO de tipo CONSUMOS");
        }

        Consumo consumo = mapearDesdeRequestDTO(dto, factura);
        consumoRepository.save(consumo);

        facturaService.recalcularSubtotal(factura);

        return mapearAResponseDTO(consumo);
    }
    //endregion

    //region Actualizar un consumo existente (siempre que la factura esté EN_PROCESO).
    public ConsumoResponseDTO actualizarConsumo(Long consumoId, ConsumoRequestDTO dto) {
        Consumo consumo = consumoRepository.findById(consumoId)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado"));

        Factura factura = consumo.getFactura();

        if (!factura.getEstado().equals(EstadoFactura.EN_PROCESO)) {
            throw new IllegalStateException("No se puede modificar consumos de una factura cerrada");
        }

        consumo.setDescripcion(dto.getDescripcion());
        consumo.setCantidad(dto.getCantidad());
        consumo.setPrecioUnitario(dto.getPrecioUnitario());
        consumo.setSubtotal(dto.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.getCantidad())));

        consumoRepository.save(consumo);

        facturaService.recalcularSubtotal(factura);

        return mapearAResponseDTO(consumo);
    }
    //endregion

    //region Eliminar un consumo.
    public void eliminarConsumo(Long consumoId) {
        Consumo consumo = consumoRepository.findById(consumoId)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado"));

        Factura factura = consumo.getFactura();

        if (!factura.getEstado().equals(EstadoFactura.EN_PROCESO)) {
            throw new IllegalStateException("No se puede eliminar consumos de una factura cerrada");
        }

        consumoRepository.delete(consumo);
        facturaService.recalcularSubtotal(factura);
    }
    //endregion

    //region Listar consumos por factura.
    public List<ConsumoResponseDTO> listarPorFacturaDTO(Long facturaId) {
        return consumoRepository.findByFacturaId(facturaId)
                .stream()
                .map(this::mapearAResponseDTO)
                .toList();
    }
    //endregion

    //region Listar consumos por reserva.
    public List<ConsumoResponseDTO> listarPorReservaDTO(Long reservaId) {
        Factura factura = facturaService.obtenerFacturaConsumosPorReserva(reservaId);
        return consumoRepository.findByFacturaId(factura.getId())
                .stream()
                .map(this::mapearAResponseDTO)
                .toList();
    }
    //endregion
}
