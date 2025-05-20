package RestInn.entities.cobranzas;

import RestInn.entities.Reserva;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Tarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double descuento;
    private Double impuesto;
    private Reserva reserva;
}
