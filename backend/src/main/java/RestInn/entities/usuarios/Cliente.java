package RestInn.entities.usuarios;

import RestInn.entities.cobranzas.Factura;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true) //IMPORTANTE: constructor sin args para JPA y evitar error de constructor no definido
@Getter
@Setter
@DiscriminatorValue("Cliente")
public class Cliente extends Usuario {
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Factura> facturas;
}