package RestInn.entities;

import RestInn.entities.enums.EstadoReserva;
import RestInn.entities.usuarios.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Usuario (muchas reservas pueden ser de un usuario)
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id") // nombre de la columna FK
    private Usuario usuario;

    // Relación con Habitación (muchas reservas pueden ser de una habitación)
    @ManyToOne(optional = false)
    @JoinColumn(name = "habitacion_id")
    private Habitacion habitacion;

    // Relación con Huespedes (una reserva tiene muchos huéspedes)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reserva_id") // clave foránea en Huesped apuntando a esta reserva
    private List<Huesped> huespedes;

    // Enum de estado
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estadoReserva;

    @Column(name = "fecha_reserva", nullable = true)
    private LocalDate fechaReserva;

    @Column(nullable = false, name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(nullable = false, name = "fecha_salida")
    private LocalDate fechaSalida;
}