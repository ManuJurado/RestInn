package RestInn.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Imagen {
    private Long imagenId;
    private String nombre;

    private String tipoImagen; // ej: "image/png"

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] datos;
}