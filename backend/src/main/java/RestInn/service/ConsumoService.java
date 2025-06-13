package RestInn.service;

import RestInn.dto.cobranzasDTO.ConsumoRequestDTO;
import RestInn.dto.cobranzasDTO.ConsumoResponseDTO;
import RestInn.entities.cobranzas.Consumo;
import RestInn.entities.cobranzas.Factura;
import RestInn.entities.enums.EstadoFactura;
import RestInn.entities.enums.TipoFactura;
import RestInn.repositories.ConsumoRepository;
import RestInn.repositories.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ConsumoService {

    @Autowired
    private ConsumoRepository consumoRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private FacturaService facturaService;

    //region Mapear DTO → Entidad
    private Consumo mapearDesdeRequestDTO(ConsumoRequestDTO dto, Factura factura) {
        BigDecimal subtotal = dto.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(dto.getCantidad()));
        return Consumo.builder()
                .descripcion(dto.getDescripcion())
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .subtotal(subtotal)
                .factura(factura)
                .build();
    }
    //endregion

    //region Mapear Entidad → DTO
    private ConsumoResponseDTO mapearAResponseDTO(Consumo consumo) {
        return ConsumoResponseDTO.builder()
                .id(consumo.getId())
                .descripcion(consumo.getDescripcion())
                .cantidad(consumo.getCantidad())
                .precioUnitario(consumo.getPrecioUnitario())
                .subtotal(consumo.getSubtotal())
                .build();
    }
    //endregion

    //region Recuperar factura o lanzar excepción
    private Factura cargarFacturaEntidad(Long facturaId) {
        return facturaRepository.findById(facturaId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Factura no encontrada: " + facturaId));
    }
    //endregion

    //region Crear Consumo
    @Transactional
    public ConsumoResponseDTO crearConsumo(Long facturaId, ConsumoRequestDTO dto) {
        Factura factura = cargarFacturaEntidad(facturaId);
        if (factura.getTipoFactura() != TipoFactura.CONSUMOS ||
                factura.getEstado()     != EstadoFactura.EN_PROCESO) {
            throw new IllegalStateException(
                    "Solo se pueden agregar consumos a facturas EN_PROCESO de tipo CONSUMOS");
        }

        Consumo consumo = mapearDesdeRequestDTO(dto, factura);
        consumoRepository.save(consumo);

        // Recalcula y guarda en facturaService
        facturaService.recalcularSubtotal(factura);

        return mapearAResponseDTO(consumo);
    }
    //endregion

    //region Actualizar Consumo
    @Transactional
    public ConsumoResponseDTO actualizarConsumo(Long consumoId, ConsumoRequestDTO dto) {
        Consumo consumo = consumoRepository.findById(consumoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Consumo no encontrado: " + consumoId));

        Factura factura = consumo.getFactura();
        if (factura.getEstado() != EstadoFactura.EN_PROCESO) {
            throw new IllegalStateException(
                    "No se puede modificar consumos de una factura cerrada");
        }

        consumo.setDescripcion(dto.getDescripcion());
        consumo.setCantidad(dto.getCantidad());
        consumo.setPrecioUnitario(dto.getPrecioUnitario());
        consumo.setSubtotal(dto.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(dto.getCantidad())));

        consumoRepository.save(consumo);

        facturaService.recalcularSubtotal(factura);

        return mapearAResponseDTO(consumo);
    }
    //endregion

    //region Eliminar Consumo
    @Transactional
    public void eliminarConsumo(Long consumoId) {
        Consumo consumo = consumoRepository.findById(consumoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Consumo no encontrado: " + consumoId));

        Factura factura = consumo.getFactura();
        if (factura.getEstado() != EstadoFactura.EN_PROCESO) {
            throw new IllegalStateException(
                    "No se puede eliminar consumos de una factura cerrada");
        }

        consumoRepository.delete(consumo);
        facturaService.recalcularSubtotal(factura);
    }
    //endregion

    //region Listar por factura
    @Transactional(readOnly = true)
    public List<ConsumoResponseDTO> listarPorFacturaDTO(Long facturaId) {
        // Verifica existencia de factura
        cargarFacturaEntidad(facturaId);

        return consumoRepository.findByFacturaId(facturaId).stream()
                .map(this::mapearAResponseDTO)
                .toList();
    }
    //endregion

    //region Listar por reserva
    @Transactional(readOnly = true)
    public List<ConsumoResponseDTO> listarPorReservaDTO(Long reservaId) {
        // Obtiene la factura de consumos asociada a la reserva
        Factura factura = facturaService.obtenerFacturaConsumosPorReserva(reservaId);

        return consumoRepository.findByFacturaId(factura.getId()).stream()
                .map(this::mapearAResponseDTO)
                .toList();
    }
    //endregion
}
