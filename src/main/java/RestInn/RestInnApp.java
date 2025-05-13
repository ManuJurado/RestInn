package RestInn;

import RestInn.service.HabitacionService;
import RestInn.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "RestInn.entities")
@EnableJpaRepositories(basePackages = "RestInn.repositories")
public class RestInnApp implements CommandLineRunner {

	private final ReservaService reservaService;
	private final HabitacionService habitacionService;

	@Autowired
	public RestInnApp(HabitacionService habitacionService, ReservaService reservaService) {
		this.habitacionService = habitacionService;
		this.reservaService = reservaService;

	}

	public static void main(String[] args) {
		SpringApplication.run(RestInnApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}