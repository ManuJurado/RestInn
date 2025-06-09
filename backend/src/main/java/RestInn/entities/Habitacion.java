package RestInn.entities;

import RestInn.entities.enums.H_Estado;
import RestInn.entities.enums.H_Tipo;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "boolean default true", nullable = false)
    private Boolean activo;     // Borrado Lógico.

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private H_Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private H_Tipo tipo;

        @Column(nullable = false, unique = true)
    private Integer numero;

        @Column(nullable = false)
    private Integer piso;

        @Column(nullable = false)
    private Integer capacidad;

        @Column(name = "cantidad_camas",
                nullable = false)
    private Integer cantCamas;

        @Column(nullable = false)
        @NotNull(message = "El precio por noche no puede ser nulo.")
        @DecimalMin(value = "0.01", message = "El precio por noche debe ser mayor que 0.")
    private BigDecimal precioNoche;

    private String comentario;

    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Imagen> imagenes;
}
