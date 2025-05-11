package RestInn;

import RestInn.service.ClienteService;
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

	@Autowired
	public RestInnApp(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	public static void main(String[] args) {
		SpringApplication.run(RestInnApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Crear un cliente para probar la inserción de datos
		clienteService.crearCliente("Emi Salias", "emisalias@gmail.com");
		System.out.println("Cliente creado con éxito!");
	}
}