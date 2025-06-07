package RestInn.dto.habitacionesDTO;

import RestInn.entities.Imagen;
import lombok.Getter;
import lombok.Setter;
import java.util.Base64;

@Getter
@Setter
public class ImagenBase64DTO {
    private Long id;
    private String nombre;
    private String tipo;
    private String datosBase64;

    public ImagenBase64DTO(Imagen imagen) {
        this.id = imagen.getId();
        this.nombre = imagen.getNombre();
        this.tipo = imagen.getTipo();
        this.datosBase64 = Base64.getEncoder().encodeToString(imagen.getDatos());
    }
}
