package RestInn.repositories.specifications;

import RestInn.entities.Habitacion;
import RestInn.entities.enums.H_Estado;
import org.springframework.data.jpa.domain.Specification;

public class HabitacionSprecification {
    public static Specification<Habitacion> tieneTipo(H_Estado tipo) {
        return (root, query, criteriaBuilder) ->
                tipo == null ? null :
                        criteriaBuilder.equal(root.get("tipo"), tipo);
    }
    public static Specification<Habitacion> tieneCapacidad(Integer capacidad) {
        return (root, query, criteriaBuilder) ->
                capacidad == null ? null :
                        criteriaBuilder.equal(root.get("capacidad"), capacidad);
    }
    public static Specification<Habitacion> precioNocheMenorA(Double precioNoche) {
        return (root, query, criteriaBuilder) ->
                precioNoche == null ? null :
                        criteriaBuilder.lessThanOrEqualTo(root.get("precioNoche"), precioNoche);
    }
    public static Specification<Habitacion> tieneCantCamas(Integer cantCamas) {
        return (root, query, criteriaBuilder) ->
                cantCamas == null ? null :
                        criteriaBuilder.greaterThanOrEqualTo(root.get("cantCamas"), cantCamas);
    }
}
