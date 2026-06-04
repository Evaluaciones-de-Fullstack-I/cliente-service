package cl.duoc.clientes.controller;

import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    private final WebClient webClient;

    public ClienteController(ClienteService clienteService, WebClient webClient) {
        this.clienteService = clienteService;
        this.webClient = webClient;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.getClientes());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> agregarCliente(@Valid @RequestBody CreateRequestCliente request) {
        Cliente nuevoCliente = clienteService.saveCliente(ClienteMapper.toCliente(request));

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cliente creado correctamente");
        response.put("id", nuevoCliente.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // RECORDATORIO: Endpoint corregido. Envía un JSON dinámico y un mensaje personalizado si el cliente es histórico
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            Cliente cliente = clienteService.login(request.getCorreo(), request.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", cliente.getId());
            response.put("nombre", cliente.getNombre());
            response.put("correo", cliente.getCorreo());
            response.put("rol", cliente.getRol());
            response.put("tipoCliente", cliente.getTipoCliente());
            
            if ("LEGACY".equals(cliente.getTipoCliente())) {
                response.put("mensaje", "¡Bienvenida de vuelta! Has iniciado sesión como Cliente Histórico de Almacenes Paris.");
            } else {
                response.put("mensaje", "Login exitoso.");
            }
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            // RECORDATORIO: Atrapa las claves malas devolviendo Error 401 (Unauthorized) en vez de un feo Error 500
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarCliente(@PathVariable int id) {
        Cliente cliente = clienteService.getClienteId(id);
        if (cliente == null) {
            throw new ResourceNotFoundException("Cliente con id=" + id + " no encontrado");
        }
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarCliente(@PathVariable int id, @Valid @RequestBody UpdateRequestCliente request) {
        Cliente clienteActualizado = clienteService.updateCliente(id, request);
        if (clienteActualizado == null) {
            throw new ResourceNotFoundException("Cliente con id=" + id + " no encontrado");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cliente actualizado correctamente");
        response.put("id", clienteActualizado.getId());
        return ResponseEntity.ok(response);
    }

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
  
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<Cliente>> buscarPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(clienteService.buscarPorRol(rol));
    }

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

    @GetMapping("/admins")
    public ResponseEntity<Map<String, Object>> obtenerAdmins() {
        List<Cliente> admins = clienteService.obtenerAdmins();
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Administradores obtenidos correctamente");
        response.put("total", admins.size());
        response.put("admins", admins);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{clienteId}/carrito")
    public ResponseEntity<String> agregarAlCarrito(@PathVariable Integer clienteId) {
        try {
            webClient.post()
                    .uri("http://localhost:8086/api/v1/carritos")
                    .bodyValue(Map.of(
                            "clienteId", clienteId,
                            "productoId", 1, 
                            "cantidad", 1
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println(" CLIENTE recibió respuesta de CARRITO");
            return ResponseEntity.ok("Producto agregado al carrito");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al comunicarse con carrito");
        }
    }
}