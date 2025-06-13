package RestInn.service;

import RestInn.dto.cobranzasDTO.ConsumoResponseDTO;
import RestInn.dto.cobranzasDTO.FacturaRequestDTO;
import RestInn.dto.cobranzasDTO.FacturaResponseDTO;
import RestInn.entities.Reserva;
import RestInn.entities.cobranzas.Consumo;
import RestInn.entities.cobranzas.Factura;
import RestInn.entities.enums.EstadoFactura;
import RestInn.entities.enums.MetodoPago;
import RestInn.entities.enums.TipoFactura;
import RestInn.entities.usuarios.Cliente;
import RestInn.repositories.FacturaRepository;
import RestInn.repositories.ReservaRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    //region Mapear entidad → DTO
    public FacturaResponseDTO mapearAResponseDTO(Factura factura) {
        List<ConsumoResponseDTO> consumosDTO = factura.getConsumos() != null
                ? factura.getConsumos().stream()
                .map(c -> ConsumoResponseDTO.builder()
                        .id(c.getId())
                        .descripcion(c.getDescripcion())
                        .cantidad(c.getCantidad())
                        .precioUnitario(c.getPrecioUnitario())
                        .subtotal(c.getSubtotal())
                        .build())
                .toList()
                : List.of();

        return FacturaResponseDTO.builder()
                .id(factura.getId())
                .clienteNombre(factura.getCliente().getNombre())
                .ingreso(factura.getReserva().getFechaIngreso())
                .salida(factura.getReserva().getFechaSalida())
                .habitacionNumero(
                        String.valueOf(factura.getReserva().getHabitacion().getNumero()))
                .reservaId(factura.getReserva().getId())
                .fechaEmision(factura.getFechaEmision())
                .tipoFactura(factura.getTipoFactura())
                .estado(factura.getEstado())
                .subtotal(factura.getSubtotal())
                .metodoPago(factura.getMetodoPago())
                .cuotas(factura.getCuotas())
                .descuento(factura.getDescuento())
                .interes(factura.getInteres())
                .totalFinal(factura.getTotalFinal())
                .consumos(consumosDTO)
                .build();
    }
    //endregion

    //region Crear factura RESERVA
    @Transactional
    public FacturaResponseDTO generarFacturaReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reserva no encontrada: " + reservaId));

        // Cálculo de subtotal
        BigDecimal subtotal = calcularSubtotalReserva(reserva);
        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Subtotal negativo calculado");
        }

        Factura factura = Factura.builder()
                .cliente((Cliente) reserva.getUsuario())
                .reserva(reserva)
                .fechaEmision(LocalDate.now())
                .tipoFactura(TipoFactura.RESERVA)
                .estado(EstadoFactura.EMITIDA)
                .metodoPago(MetodoPago.EFECTIVO)
                .cuotas(1)
                .subtotal(subtotal)
                .descuento(BigDecimal.ZERO)
                .interes(BigDecimal.ZERO)
                .totalFinal(subtotal)
                .build();

        // Validaciones finales
        validarEntidad(factura);

        facturaRepository.save(factura);
        return mapearAResponseDTO(factura);
    }
    //endregion

    //region Crear factura CONSUMOS (inicial)
    @Transactional
    public FacturaResponseDTO generarFacturaConsumos(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reserva no encontrada: " + reservaId));

        Factura factura = Factura.builder()
                .cliente((Cliente) reserva.getUsuario())
                .reserva(reserva)
                .fechaEmision(LocalDate.now())
                .tipoFactura(TipoFactura.CONSUMOS)
                .estado(EstadoFactura.EN_PROCESO)
                .subtotal(BigDecimal.ZERO)
                .descuento(BigDecimal.ZERO)
                .interes(BigDecimal.ZERO)
                .totalFinal(BigDecimal.ZERO)
                .build();

        validarEntidad(factura);

        facturaRepository.save(factura);
        return mapearAResponseDTO(factura);
    }
    //endregion

    //region Emitir factura CONSUMOS
    @Transactional
    public FacturaResponseDTO emitirFacturaConsumos(
            Long reservaId, MetodoPago metodoPago, Integer cuotas) {

        Factura factura = facturaRepository
                .findByReservaIdAndTipoFactura(reservaId, TipoFactura.CONSUMOS)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Factura de consumos no encontrada"));

        // Sólo EN_PROCESO permitido
        if (factura.getEstado() != EstadoFactura.EN_PROCESO) {
            throw new IllegalStateException(
                    "La factura ya fue emitida o anulada");
        }

        factura.setMetodoPago(metodoPago);
        factura.setCuotas(cuotas);
        factura.setFechaEmision(LocalDate.now());

        BigDecimal total = calcularTotalConMetodoPago(factura);
        factura.setTotalFinal(total);
        factura.setEstado(EstadoFactura.EMITIDA);

        validarEntidad(factura);

        facturaRepository.save(factura);
        return mapearAResponseDTO(factura);
    }
    //endregion

    //region Actualizar metodo de pago y cuotas
    @Transactional
    public FacturaResponseDTO actualizarFactura(
            Long id, FacturaRequestDTO dto) {

        Factura factura = obtenerFacturaPorId(id);

        if (factura.getEstado() != EstadoFactura.EN_PROCESO) {
            throw new IllegalStateException(
                    "Solo se pueden modificar facturas EN_PROCESO");
        }

        factura.setMetodoPago(dto.getMetodoPago());
        factura.setCuotas(dto.getCuotas());
        factura.setFechaEmision(LocalDate.now());

        BigDecimal total = calcularTotalConMetodoPago(factura);
        factura.setTotalFinal(total);

        validarEntidad(factura);

        facturaRepository.save(factura);
        return mapearAResponseDTO(factura);
    }
    //endregion

    //region Anular factura
    @Transactional
    public void anularFactura(Long id) {
        Factura factura = obtenerFacturaPorId(id);
        factura.setEstado(EstadoFactura.ANULADA);
        validarEntidad(factura);
        facturaRepository.save(factura);
    }
    //endregion

    //region Listados y búsquedas
    public FacturaResponseDTO buscarPorReservaId(Long reservaId) {
        Factura f = facturaRepository
                .findByReservaIdAndTipoFactura(reservaId, TipoFactura.RESERVA)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe factura para la reserva"));
        return mapearAResponseDTO(f);
    }
    //endregion

    //region Buscar por factura id
    public FacturaResponseDTO buscarPorId(Long id) {
        Optional<Factura> opt = facturaRepository.findById(id);
        return opt.map(this::mapearAResponseDTO).orElse(null);
    }
    //endregion

    //region Listar todas las facturas
    public List<FacturaResponseDTO> listarTodasDTO() {
        return facturaRepository.findAll().stream()
                .map(this::mapearAResponseDTO)
                .toList();
    }
    //endregion

    //region Listar factura por reserva id
    public List<FacturaResponseDTO> listarPorReservaDTO(Long reservaId) {
        return facturaRepository.findByReservaId(reservaId).stream()
                .map(this::mapearAResponseDTO)
                .toList();
    }
    //endregion

    //region Listar por un cliente con cliente id
    public List<FacturaResponseDTO> listarPorClienteDTO(Long clienteId) {
        return facturaRepository
                .findByReserva_Usuario_Id(clienteId).stream()
                .map(this::mapearAResponseDTO)
                .toList();
    }
    //endregion

    //region Helpers de negocio
    private Factura obtenerFacturaPorId(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Factura no encontrada: " + id));
    }
    //endregion

    //region Calcular subtotal de una reserva
    private BigDecimal calcularSubtotalReserva(Reserva reserva) {
        long dias = ChronoUnit.DAYS.between(
                reserva.getFechaIngreso(), reserva.getFechaSalida());
        return reserva.getHabitacion().getPrecioNoche()
                .multiply(BigDecimal.valueOf(dias));
    }
    //endregion

    //region Recalcula el subtotal de una factura por consumos
    @Transactional
    public void recalcularSubtotal(Factura factura) {
        BigDecimal nuevoSubtotal = factura.getConsumos().stream()
                .map(c -> c.getSubtotal() != null ? c.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        factura.setSubtotal(nuevoSubtotal);
        facturaRepository.save(factura);
    }
    //endregion

    //region Obtiene factura de consumos por reserva
    @Transactional(readOnly = true)
    public Factura obtenerFacturaConsumosPorReserva(Long reservaId) {
        return facturaRepository.findByReservaIdAndTipoFactura(reservaId, TipoFactura.CONSUMOS)
                .orElseThrow(() ->
                        new IllegalArgumentException("No existe factura de consumos para la reserva ID: " + reservaId));
    }
    //endregion

    //region Calcular el total con metodo de pago
    private BigDecimal calcularTotalConMetodoPago(Factura factura) {
        BigDecimal base = factura.getSubtotal();
        if (factura.getMetodoPago() == MetodoPago.EFECTIVO) {
            BigDecimal desc = BigDecimal.valueOf(10); // 10%
            factura.setDescuento(desc);
            return base.subtract(base.multiply(desc).divide(BigDecimal.valueOf(100)));
        }
        // Crédito
        BigDecimal interes = calcularInteresPorCuotas(factura.getCuotas());
        factura.setInteres(interes);
        return base.add(base.multiply(interes).divide(BigDecimal.valueOf(100)));
    }
    //endregion

    //region Calcular interes por cantidad de cuotas
    private BigDecimal calcularInteresPorCuotas(Integer cuotas) {
        if (cuotas == null || cuotas <= 1) return BigDecimal.ZERO;
        if (cuotas <= 3) return BigDecimal.valueOf(5);
        if (cuotas <= 6) return BigDecimal.valueOf(10);
        return BigDecimal.valueOf(15);
    }
    //endregion

    //region Validación cruzada final de la entidad antes de persistir. Lanza IllegalStateException si algo crítico falta o es inválido.
    private void validarEntidad(Factura factura) {
        if (factura.getCliente() == null) {
            throw new IllegalStateException("Factura sin cliente");
        }
        if (factura.getReserva() == null) {
            throw new IllegalStateException("Factura sin reserva");
        }
        if (factura.getFechaEmision() == null ||
                factura.getFechaEmision().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Fecha de emisión inválida");
        }
        if (factura.getTipoFactura() == null) {
            throw new IllegalStateException("Factura sin tipo");
        }
        if (factura.getEstado() == null) {
            throw new IllegalStateException("Factura sin estado");
        }
        if (factura.getSubtotal() == null ||
                factura.getSubtotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Subtotal inválido");
        }
        if (factura.getMetodoPago() == null) {
            throw new IllegalStateException("Método de pago inválido");
        }
        if (factura.getCuotas() == null || factura.getCuotas() < 1) {
            throw new IllegalStateException("Cuotas inválidas");
        }
        if (factura.getTotalFinal() == null ||
                factura.getTotalFinal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Total final inválido");
        }
    }
    //endregion

    //region Generar un pdf en bytes para guardar la factura.
    public byte[] generarPdf(Long facturaId) {

        Factura factura = obtenerFacturaPorId(facturaId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font h1      = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font normal  = new Font(Font.HELVETICA, 12);
            Font bold    = new Font(Font.HELVETICA, 12, Font.BOLD);

            /* ───────── encabezado ───────── */
            doc.add(new Paragraph("RestInn – Factura Nº " + factura.getId(), h1));
            doc.add(new Paragraph("Fecha de emisión: " + factura.getFechaEmision(), normal));

            // ← NUEVO: mostrar estado factura
            doc.add(new Paragraph("Estado: " + factura.getEstado().name(), normal));
            doc.add(new Paragraph("Tipo de factura: " + factura.getTipoFactura().name(), normal));

            doc.add(Chunk.NEWLINE);

            /* ───────── datos del cliente ───────── */
            Cliente c = factura.getCliente();
            doc.add(new Paragraph("Cliente: " + c.getNombre() + " " + c.getApellido()
                    + "  (usuario: " + c.getNombreLogin() + ")", normal));
            doc.add(new Paragraph("E-mail: " + c.getEmail(), normal));
            doc.add(Chunk.NEWLINE);

            /* ───────── datos de la reserva ───────── */
            Reserva r = factura.getReserva();
            doc.add(new Paragraph("Reserva # " + r.getId()
                    + " – Habitación " + r.getHabitacion().getNumero(), bold));
            doc.add(new Paragraph("Ingreso: " + r.getFechaIngreso()
                    + "    |    Salida: " + r.getFechaSalida(), normal));
            doc.add(Chunk.NEWLINE);

            /* ───────── detalle si es factura de CONSUMOS ───────── */
            if (factura.getTipoFactura() == TipoFactura.CONSUMOS) {

                PdfPTable tbl = new PdfPTable(4);
                tbl.setWidths(new int[]{45, 10, 20, 25});
                tbl.setWidthPercentage(100f);

                // cabeceras
                headerCell(tbl,"Descripción");
                headerCell(tbl,"Cant.");
                headerCell(tbl,"P. unit");
                headerCell(tbl,"Subtotal");

                for (Consumo con : factura.getConsumos()) {
                    bodyCell(tbl, con.getDescripcion());
                    bodyCell(tbl, con.getCantidad().toString());
                    bodyCell(tbl, "$ " + con.getPrecioUnitario());
                    bodyCell(tbl, "$ " + con.getSubtotal());
                }
                doc.add(tbl);
                doc.add(Chunk.NEWLINE);
            }

            /* ───────── totales ───────── */
            PdfPTable tot = new PdfPTable(2);
            tot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tot.setWidths(new int[]{70, 30});
            tot.setTotalWidth(200);
            tot.setLockedWidth(true);

            bodyCell(tot,"Subtotal");
            bodyCell(tot,"$ " + factura.getSubtotal());

            if (factura.getDescuento()!=null && factura.getDescuento().signum()>0) {
                bodyCell(tot,"Descuento ("+factura.getDescuento()+"%)");
                BigDecimal desc = factura.getSubtotal()
                        .multiply(factura.getDescuento())
                        .divide(BigDecimal.valueOf(100));
                bodyCell(tot,"- $ " + desc);
            }
            if (factura.getInteres()!=null && factura.getInteres().signum()>0) {
                bodyCell(tot,"Interés ("+factura.getInteres()+"%)");
                BigDecimal intes = factura.getSubtotal()
                        .multiply(factura.getInteres())
                        .divide(BigDecimal.valueOf(100));
                bodyCell(tot,"+ $ " + intes);
            }

            bodyCell(tot,"TOTAL", bold, Color.LIGHT_GRAY);
            bodyCell(tot,"$ " + factura.getTotalFinal(), bold, Color.LIGHT_GRAY);

            doc.add(tot);
            doc.add(Chunk.NEWLINE);

            doc.add(new Paragraph("Método de pago: "
                    + factura.getMetodoPago()
                    + (factura.getCuotas()!=null && factura.getCuotas()>1
                    ? " – " + factura.getCuotas() + " cuotas" : ""),
                    normal));

            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }

        return baos.toByteArray();
    }


    /* helpers internos para celdas */
    private void headerCell(PdfPTable t, String txt){
        PdfPCell c = new PdfPCell(new Phrase(txt, new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE)));
        c.setBackgroundColor(new Color(60, 60, 60));
        t.addCell(c);
    }
    private void bodyCell(PdfPTable t, String txt){
        bodyCell(t, txt, new Font(Font.HELVETICA, 11), null);
    }
    private void bodyCell(PdfPTable t, String txt, Font f, Color bg){
        PdfPCell c = new PdfPCell(new Phrase(txt, f));
        if (bg!=null) c.setBackgroundColor(bg);
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(c);
    }
    //endregion

    //region ACTUALIZA EL ESTADO DE LA FACTURA
    @Transactional
    public void actualizarFacturaEstado(Factura factura) {
        facturaRepository.save(factura);
    }
    //endregion

}
