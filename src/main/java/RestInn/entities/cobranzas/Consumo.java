package RestInn.entities.cobranzas;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Consumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double precioUnitario;
    private Integer cantidad;
    private String detalle;
    private Double montoTotal;
}
