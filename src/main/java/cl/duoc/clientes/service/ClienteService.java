package cl.duoc.clientes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cl.duoc.clientes.repository.ClienteRepository;
import cl.duoc.clientes.model.Cliente;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
@Service

public class ClienteService {
    @Autowired//inicializa  para el constructor en el momento de crear el objeto
    private ClienteRepository clienteRepository;
    
    private WebClient webClient;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }
    // LISTAR
    public List<Cliente> getClientes(){

        return clienteRepository.findAll();
    }
// LOGIN
public Cliente login(String correo, String password) {

    return webClient.post()
            .uri("http://legacy-system/login")
            .retrieve()
            .bodyToMono(Cliente.class)
            .block();
}



    // GUARDAR
    public Cliente saveCliente(Cliente cliente){

        return clienteRepository.save(cliente);
    }

    // BUSCAR POR ID
    public Cliente getClienteId(int id){

        return clienteRepository.findById((int) id)
                .orElse(null);
    }

    // ACTUALIZAR
    public Cliente updateCliente(Cliente cliente){

        return clienteRepository.save(cliente);
    }

    // ELIMINAR
    public void deleteCliente(int id){

        clienteRepository.deleteById((int) id);
    }

    // BUSCAR POR ROL
    public List<Cliente> buscarPorRol(String rol){

        return clienteRepository.findByRol(rol);
    }

    // BUSCAR POR CORREO
    public Cliente buscarPorCorreo(String correo){

        return clienteRepository.findByCorreo(correo);
    }

    // OBTENER ADMINS
    public List<Cliente> obtenerAdmins(){

        return clienteRepository.findByRol("ADMIN");
    }

    // VERIFICAR CORREO
    public boolean existeCorreo(String correo){

        return clienteRepository.existsByCorreo(correo);
    }

}