package RestInn;

import RestInn.config.EnvironmentConfig;
import RestInn.service.HabitacionService;
import RestInn.service.ReservaService;
import RestInn.service.UsuarioService;
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
	private final HabitacionService habitacionService;
	private final UsuarioService usuarioService;
	private final ReservaService reservaService;

	@Autowired
	public RestInnApp(HabitacionService habitacionService,
					  UsuarioService usuarioService,
					  ReservaService reservaService) {
		this.habitacionService = habitacionService;
		this.usuarioService = usuarioService;
		this.reservaService = reservaService;
	}

	public static void main(String[] args) {
		EnvironmentConfig config = new EnvironmentConfig(); // Fuerza la carga del static block
		SpringApplication.run(RestInnApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
