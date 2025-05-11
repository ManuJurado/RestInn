package RestInn;

import RestInn.service.ClienteService;
import RestInn.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "RestInn.entity")
@EnableJpaRepositories(basePackages = "RestInn.repository")
public class RestInnApp implements CommandLineRunner {

	private final ClienteService clienteService;
	private final HabitacionService habitacionService;

	@Autowired
	public RestInnApp(ClienteService clienteService, HabitacionService habitacionService) {
		this.clienteService = clienteService;
		this.habitacionService = habitacionService;
	}


	public static void main(String[] args) {
		SpringApplication.run(RestInnApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		habitacionService.crearHabitacion();
		System.out.println("Habitacion creada con éxito!");
	}
}