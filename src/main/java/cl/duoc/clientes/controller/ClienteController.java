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



// 🟢 IMPORTACIONES DE OPENAPI / SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final WebClient webClient;

    public ClienteController(ClienteService clienteService, WebClient webClient) {
        this.clienteService = clienteService;
        this.webClient = webClient;
    }

    // LISTAR CLIENTES
    @GetMapping
    @Operation(summary = "Listar clientes", description = "Recupera la lista completa de todos los clientes registrados en la base de datos.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida de forma exitosa")
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.getClientes());
    }

    // AGREGAR CLIENTE
    @PostMapping
    @Operation(summary = "Registrar un nuevo cliente", description = "Crea un nuevo usuario en el sistema. Por defecto se inicializa como cliente NUEVO a menos que se indique lo contrario.")
    @ApiResponse(responseCode = "201", description = "Cliente creado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos de registro inválidos")
    public ResponseEntity<Map<String, Object>> agregarCliente(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Campos necesarios para registrar el perfil de un cliente",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreateRequestCliente.class),
                examples = @ExampleObject(
                    name = "Ejemplo Registro Cliente",
                    value = "{\n  \"nombre\": \"María González\",\n  \"correo\": \"maria.gonzalez@email.cl\",\n  \"password\": \"segura123\",\n  \"tipoCliente\": \"NUEVO\"\n}"
                )
            )
        )
        @Valid @RequestBody CreateRequestCliente request
    ) {
        Cliente nuevoCliente = clienteService.saveCliente(ClienteMapper.toCliente(request));

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cliente creado correctamente");
        response.put("id", nuevoCliente.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // LOGIN DE USUARIOS (MANEJA LEGACY / NUEVO)
    @PostMapping("/login")
    @Operation(summary = "Autenticación de cliente", description = "Valida las credenciales de ingreso. Reconoce de forma dinámica a los clientes históricos LEGACY (Almacenes Paris) inyectando un mensaje de bienvenida especial.")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas (Unauthorized)")
    public ResponseEntity<Map<String, Object>> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciales de acceso",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Intento Login Cliente Histórico (LEGACY)",
                        value = "{\n  \"correo\": \"paris.historico@paris.cl\",\n  \"password\": \"123456\"\n}"
                    ),
                    @ExampleObject(
                        name = "Intento Login Cliente Estándar (NUEVO)",
                        value = "{\n  \"correo\": \"maria.gonzalez@email.cl\",\n  \"password\": \"segura123\"\n}"
                    )
                }
            )
        )
        @RequestBody LoginRequest request
    ) {
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Recupera la ficha de datos de un cliente según su ID único.")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<Cliente> buscarCliente(@PathVariable int id) {
        Cliente cliente = clienteService.getClienteId(id);
        if (cliente == null) {
            throw new ResourceNotFoundException("Cliente con id=" + id + " no encontrado");
        }
        return ResponseEntity.ok(cliente);
    }

    // ACTUALIZAR CLIENTE
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar perfil de cliente", description = "Modifica los datos personales o de configuración de un cliente.")
    @ApiResponse(responseCode = "200", description = "Cambios guardados correctamente")
    @ApiResponse(responseCode = "404", description = "El cliente a actualizar no existe")
    public ResponseEntity<Map<String, Object>> actualizarCliente(
        @PathVariable int id, 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Campos editables del perfil",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateRequestCliente.class),
                examples = @ExampleObject(
                    name = "Ejemplo Modificar Cliente",
                    value = "{\n  \"nombre\": \"María González Ramos\"\n}"
                )
            )
        )
        @Valid @RequestBody UpdateRequestCliente request
    ) {
        Cliente clienteActualizado = clienteService.updateCliente(id, request);
        if (clienteActualizado == null) {
            throw new ResourceNotFoundException("Cliente con id=" + id + " no encontrado");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cliente actualizado correctamente");
        response.put("id", clienteActualizado.getId());
        return ResponseEntity.ok(response);
    }

    // ELIMINAR CLIENTE
    @DeleteMapping("/{id}")
    @Operation(summary = "Dar de baja cliente", description = "Remueve permanentemente el registro de un cliente del sistema.")
    @ApiResponse(responseCode = "200", description = "Cliente eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
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
    @Operation(summary = "Filtrar clientes por Rol", description = "Devuelve un listado de clientes filtrados por su rol asignado (ej: USER, ADMIN).")
    @ApiResponse(responseCode = "200", description = "Listado de usuarios filtrado obtenido")
    public ResponseEntity<List<Cliente>> buscarPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(clienteService.buscarPorRol(rol));
    }

    // BUSCAR POR CORREO
    @GetMapping("/buscar")
    @Operation(summary = "Buscar cliente por correo electrónico", description = "Endpoint de consulta rápida para verificar si un email ya está registrado.")
    @ApiResponse(responseCode = "200", description = "Cliente localizado")
    @ApiResponse(responseCode = "404", description = "Correo no registrado")
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

    // OBTENER ADMINISTRADORES
    @GetMapping("/admins")
    @Operation(summary = "Listar cuentas administrativas", description = "Filtra y devuelve de forma directa las cuentas que poseen rango administrativo.")
    @ApiResponse(responseCode = "200", description = "Lista interna de administradores recuperada")
    public ResponseEntity<Map<String, Object>> obtenerAdmins() {
        List<Cliente> admins = clienteService.obtenerAdmins();
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Administradores obtenidos correctamente");
        response.put("total", admins.size());
        response.put("admins", admins);
        return ResponseEntity.ok(response);
    }

    // AGREGAR AL CARRITO (INTEGRACIÓN)
    @PostMapping("/{clienteId}/carrito")
    @Operation(summary = "Agregar producto al carrito (Integración)", description = "Llama mediante WebClient al microservicio de Carritos para inyectarle un producto de forma automatizada.")
    @ApiResponse(responseCode = "200", description = "Producto agregado al carrito con éxito")
    @ApiResponse(responseCode = "400", description = "Fallo en la comunicación síncrona entre microservicios")
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