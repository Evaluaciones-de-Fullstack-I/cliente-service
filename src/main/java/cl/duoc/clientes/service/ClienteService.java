package cl.duoc.clientes.service;

import org.springframework.stereotype.Service;
import cl.duoc.clientes.repository.ClienteRepository;
import cl.duoc.clientes.dto.UpdateRequestCliente;
import cl.duoc.clientes.exception.ResourceNotFoundException;
import cl.duoc.clientes.mapper.ClienteMapper;
import cl.duoc.clientes.model.Cliente;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Optional;

@Service
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    private final WebClient webClient;
   
    public ClienteService(ClienteRepository clienteRepository, WebClient webClient) {
        this.clienteRepository = clienteRepository;
        this.webClient = webClient;
    }

    public List<Cliente> getClientes(){
        return clienteRepository.findAll();
    }

    // RECORDATORIO: Login unificado en la misma BD. Soporta usuarios nuevos (Pedro) e históricos (María)
    public Cliente login(String correo, String password) {
        Cliente cliente = clienteRepository.findByCorreo(correo);

        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado en nuestros registros");
        }

        // RECORDATORIO: Validación directa de contraseña evitando llamadas HTTP innecesarias
        if (cliente.getPassword() != null && cliente.getPassword().equals(password)) {
            
            // RECORDATORIO: Alerta visual en la consola si el cliente que entra es histórico
            if ("LEGACY".equals(cliente.getTipoCliente())) {
                System.out.println("⭐ [HISTÓRICO] Cliente de Almacenes Paris detectado: " + cliente.getNombre());
            }
            return cliente;
        }
        throw new RuntimeException("Credenciales inválidas");
    }

    // RECORDATORIO: Escudo protector. Asegura que los registros web nuevos (Pedro) queden como "NUEVO" y nunca "LEGACY"
    public Cliente saveCliente(Cliente cliente){
        if (cliente.getTipoCliente() == null || cliente.getTipoCliente().trim().isEmpty()) {
            cliente.setTipoCliente("NUEVO");
        }
        
        if ("LEGACY".equalsIgnoreCase(cliente.getTipoCliente())) {
            cliente.setTipoCliente("NUEVO"); 
        }
        return clienteRepository.save(cliente);
    }

    public Cliente getClienteId(int id){
        return clienteRepository.findById(id).orElse(null);
    }

    public Cliente updateCliente(int id, UpdateRequestCliente request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        ClienteMapper.updateCliente(cliente, request);
        return clienteRepository.save(cliente);
    }

    public boolean deleteCliente(int id) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        if (clienteOpt.isPresent()) {
            clienteRepository.delete(clienteOpt.get());
            return true;
        } else {
            throw new ResourceNotFoundException("Cliente con id=" + id + " no encontrado");
        }
    }

    public List<Cliente> buscarPorRol(String rol){
        return clienteRepository.findByRol(rol);
    }

    public Cliente buscarPorCorreo(String correo){
        return clienteRepository.findByCorreo(correo);
    }

    public List<Cliente> obtenerAdmins(){
        return clienteRepository.findByRol("ADMIN");
    }

    public boolean existeCorreo(String correo){
        return clienteRepository.existsByCorreo(correo);
    }
}