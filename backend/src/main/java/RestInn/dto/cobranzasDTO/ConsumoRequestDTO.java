package RestInn.dto.cobranzasDTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ConsumoRequestDTO {
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
