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
import org.springframework.web.server.ResponseStatusException;
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
  public ResponseEntity<Map<String, Object>> agregarCliente(@Valid @RequestBody CreateRequestCliente request) {
        Cliente nuevoCliente = clienteService.saveCliente(ClienteMapper.toCliente(request));

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cliente creado correctamente");
        response.put("id", nuevoCliente.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
//cliente historico inicia sesion/////////////////////
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
    public ResponseEntity<Cliente> buscarCliente(@PathVariable int id) {
        Cliente cliente = clienteService.getClienteId(id);

        if (cliente == null) {
            throw new ResourceNotFoundException("Cliente con id=" + id + " no encontrado");
        }
        return ResponseEntity.ok(cliente);
    }
//actualizar cliente por id

@PutMapping("{id}")
public ResponseEntity<Map<String, Object>> actualizarCliente(
            @PathVariable int id,
            @Valid @RequestBody UpdateRequestCliente request
    ) {
        Cliente clienteActualizado = clienteService.updateCliente(id, request);

        if (clienteActualizado == null) {
            throw new ResourceNotFoundException("Cliente con id=" + id + " no encontrado");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cliente actualizado correctamente");
        response.put("id", clienteActualizado.getId());

        return ResponseEntity.ok(response);}

 // ELIMINAR CLIENTE

 @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarCliente(@PathVariable int id) {
        boolean eliminado = clienteService.deleteCliente(id);

        if (!eliminado) {
            throw new ResourceNotFoundException("Cliente con id=" + id + " no encontrado");
        }

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Cliente eliminado correctamente");

        return ResponseEntity.ok(response);
    }
  
    // BUSCAR POR ROL
    @GetMapping("/rol/{rol}")

    public ResponseEntity<List<Cliente>> buscarPorRol(@PathVariable String rol) {
        List<Cliente> clientes = clienteService.buscarPorRol(rol);
        return ResponseEntity.ok(clientes);
    }
// BUSCAR POR CORREO
  @GetMapping("/buscar")
public ResponseEntity<Map<String, Object>> buscarPorCorreo(@RequestParam String correo) {
    Cliente cliente = clienteService.buscarPorCorreo(correo);

    if (cliente == null) {
        throw new ResourceNotFoundException("Cliente con correo=" + correo + " no encontrado");
    }

    Map<String, Object> response = new HashMap<>();
    response.put("mensaje", "Cliente encontrado correctamente");
    response.put("id", cliente.getId());
    response.put("correo", cliente.getCorreo());

    return ResponseEntity.ok(response);
}
//filtrar por administradores

@GetMapping("/admins")
public ResponseEntity<Map<String, Object>> obtenerAdmins() {
    List<Cliente> admins = clienteService.obtenerAdmins();

    Map<String, Object> response = new HashMap<>();
    response.put("mensaje", "Administradores obtenidos correctamente");
    response.put("total", admins.size());
    response.put("admins", admins);

    return ResponseEntity.ok(response);
}


//co}municacion con carrito

@PostMapping("/{clienteId}/agregar-carrito")
public ResponseEntity<String> agregarAlCarrito(
        @PathVariable Integer clienteId
) {

    try {

     System.out.println(" CLIENTE enviando productos al CARRITO");

        Map<String, Object> carrito = new HashMap<>();
        carrito.put("clienteId", clienteId);
        carrito.put("productoId", 10);
        carrito.put("cantidad", 2);
        carrito.put("subtotal", 20000);
        carrito.put("estado", "ACTIVO");

        webClient.post()
                .uri("http://localhost:8086/api/v1/carritos")
                .bodyValue(carrito)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("📨 CLIENTE recibió respuesta de CARRITO");
        return ResponseEntity.ok("Producto agregado al carrito");

    } catch (Exception e) {
e.printStackTrace();
        return ResponseEntity.badRequest()
                .body("Error al comunicarse con carrito");
    }
}


}



