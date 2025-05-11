package RestInn.service;

import RestInn.entities.Habitacion;
import RestInn.entities.enums.H_Estado;
import RestInn.entities.enums.H_Tipo;
import RestInn.repositories.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class HabitacionService {
    private final HabitacionRepository habitacionRepository;

    @Autowired
    public HabitacionService(HabitacionRepository clienteRepository) {
        this.habitacionRepository = clienteRepository;
    }

    public Habitacion crearHabitacion() {
        Habitacion habitacion = new Habitacion();
        habitacion.setCantCamas(2);
        habitacion.setDisponible(true);
        habitacion.setCapacidad(3);
        habitacion.setNumero(19);
        habitacion.setDisponible(true);
        habitacion.setTipo(H_Tipo.DOBLE);
        habitacion.setEstado(H_Estado.DISPONIBLE);
        habitacion.setPrecioNoche(26000.0);
        return habitacionRepository.save(habitacion);
    }
}
