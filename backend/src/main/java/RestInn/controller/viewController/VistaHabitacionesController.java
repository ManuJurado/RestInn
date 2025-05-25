package RestInn.controller.viewController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaHabitacionesController {

    @GetMapping("/verTodasHabitaciones")
    public String verTodasHabitaciones() {
        return "habitaciones";  // devuelve el archivo estático de resources/static
    }
}
