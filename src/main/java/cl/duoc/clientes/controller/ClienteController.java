package cl.duoc.clientes.controller;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.clientes.model.Cliente;
import cl.duoc.clientes.service.ClienteService;
import cl.duoc.clientes.dto.CreateRequestCliente;
import cl.duoc.clientes.mapper.ClienteMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus; 
import cl.duoc.clientes.exception.ResourceNotFoundException;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.clientes.dto.LoginRequest;
import cl.duoc.clientes.dto.UpdateRequestCliente;
@RestController
@RequestMapping("/api/v1/clientes")

public class ClienteController {

private final ClienteService clienteService;
private final WebClient   webClient; // Inyección de WebClient

// Constructor injection (mejor práctica 2026)

public ClienteController(
        ClienteService clienteService,
        WebClient webClient
) {
    this.clienteService = clienteService;
    this.webClient = webClient;
}

//listar clientes

@GetMapping
public ResponseEntity<List<Cliente>> listarClientes() {
    List<Cliente> clientes = clienteService.getClientes();
    return ResponseEntity.ok(clientes);

   }
   //creacion de clientes
@PostMapping
 public ResponseEntity<Cliente> agregarCliente(@Valid@RequestBody CreateRequestCliente request) {
          Cliente nuevoCliente = clienteService.saveCliente(ClienteMapper.toCliente(request));
          return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
 
 
        }
//cliente historico inicia sesion
@PostMapping("/login")
public ResponseEntity<Cliente> login(
        @RequestBody LoginRequest request
) {

    Cliente cliente =
            clienteService.login(
                    request.getCorreo(),
                    request.getPassword()
            );

    return ResponseEntity.ok(cliente);
}

        
//buscar cliente por id
 @GetMapping("/{id}")
     public ResponseEntity<Cliente> buscarCliente(
            @PathVariable int id
    ) {

       Cliente cliente = clienteService.getClienteId(id);

        if(cliente == null){

            throw new ResourceNotFoundException("Cliente no encontrado por id: " + id);
        }
        return ResponseEntity.ok(cliente);
    }
//actualizar cliente por id

@PutMapping("{id}")
public ResponseEntity<Cliente> actualizarCliente(
        @PathVariable int id,
        @Valid @RequestBody UpdateRequestCliente request
) {
    Cliente clienteActualizado = clienteService.updateCliente(id, request);
    return ResponseEntity.ok(clienteActualizado);
}


 // ELIMINAR CLIENTE
    @DeleteMapping("{id}")
    public ResponseEntity<Void> eliminarCliente(
            @PathVariable int id
    ) {

        clienteService.deleteCliente(id);

        return ResponseEntity.noContent().build();
    }
    
    // BUSCAR POR ROL
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<Cliente>> buscarPorRol(
            @PathVariable String rol
    ) {

        List<Cliente> clientes =
                clienteService.buscarPorRol(rol);

        return ResponseEntity.ok(clientes);
    } 
// BUSCAR POR CORREO
    @GetMapping("/buscar")
    public ResponseEntity<Cliente> buscarPorCorreo(
            @RequestParam String correo
    ) {

        Cliente cliente =
                clienteService.buscarPorCorreo(correo);

        return ResponseEntity.ok(cliente);
    }

//filtrar por administradores

@GetMapping("/admins")
public ResponseEntity<List<Cliente>> obtenerAdmins(){

    List<Cliente> admins =
            clienteService.obtenerAdmins();

    return ResponseEntity.ok(admins);
}
//verificar si existe el correo 
@GetMapping("/existe")
public ResponseEntity<Boolean> existeCorreo(
        @RequestParam String correo
){

    boolean existe =
            clienteService.existeCorreo(correo);

    return ResponseEntity.ok(existe);
}

}



