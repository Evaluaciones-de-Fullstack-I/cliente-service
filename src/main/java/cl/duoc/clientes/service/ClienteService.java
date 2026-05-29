package cl.duoc.clientes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cl.duoc.clientes.repository.ClienteRepository;
import cl.duoc.clientes.dto.UpdateRequestCliente;
import cl.duoc.clientes.exception.ResourceNotFoundException;
import cl.duoc.clientes.mapper.ClienteMapper;
import cl.duoc.clientes.model.Cliente;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service

public class ClienteService {
    //inicializa  para el constructor en el momento de crear el objeto
    private ClienteRepository clienteRepository;
    private final WebClient webClient;
   
     public ClienteService(ClienteRepository clienteRepository, WebClient webClient) {
        this.clienteRepository = clienteRepository;
        this.webClient = webClient;
    }

   
    // LISTAR
    public List<Cliente> getClientes(){

        return clienteRepository.findAll();
    }
// LOGIN SIMULACION A LA API LEGACY 

public Cliente login(String correo, String password) {

    Cliente cliente = clienteRepository.findByCorreo(correo);

    if (cliente == null) {
        throw new RuntimeException("Cliente no encontrado");
    }

    // CLIENTE LEGACY  -PEDRO
    if (cliente.getTipoCliente().equals("LEGACY")) {

        Cliente legacyCliente = webClient.post()
                .uri("http://localhost:8080/legacy/login")
                .bodyValue(Map.of(
                        "correo", correo,
                        "password", password
                ))
                .retrieve()
                .bodyToMono(Cliente.class)
                .block();

        if (legacyCliente == null) {
            throw new RuntimeException("Credenciales legacy inválidas");
        }

        return legacyCliente;
    }

    // CLIENTE ANTIGUO  - MARIA

    if (cliente.getPassword() != null &&
    cliente.getPassword().equals(password)) {

    return cliente;
}
 throw new RuntimeException("Credenciales inválidas");

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
   public Cliente updateCliente(int id, UpdateRequestCliente request) {

    Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

    ClienteMapper.updateCliente(cliente, request);

    return clienteRepository.save(cliente);
}




    // ELIMINAR
    public boolean deleteCliente(int id) {
    Optional<Cliente> clienteOpt = clienteRepository.findById(id);

    if (clienteOpt.isPresent()) {
        clienteRepository.delete(clienteOpt.get());
        return true;
    } else {
        throw new ResourceNotFoundException("Cliente con id=" + id + " no encontrado");
    }
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