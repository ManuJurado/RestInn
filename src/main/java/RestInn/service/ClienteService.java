package RestInn.service;

import RestInn.entities.Cliente;  // Asegúrate de que esto está bien importado
import RestInn.repositories.ClienteRepository;  // Asegúrate de que esto está bien importado

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente crearCliente(String nombre, String email) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setEmail(email);
        return clienteRepository.save(cliente);
    }
}


