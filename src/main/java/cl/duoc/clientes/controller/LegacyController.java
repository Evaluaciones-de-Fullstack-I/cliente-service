package cl.duoc.clientes.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 🟢 IMPORTACIONES DE OPENAPI / SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/legacy")
public class LegacyController {

    @PostMapping("/login")
    @Operation(
        summary = "Simulación Login Sistema Legacy", 
        description = "Mock que simula la validación de credenciales en la base de datos histórica de Almacenes Paris."
    )
    @ApiResponse(responseCode = "200", description = "Credenciales válidas en el sistema antiguo (Legacy)")
    @ApiResponse(responseCode = "401", description = "Credenciales legacy inválidas")
    public ResponseEntity<Map<String, Object>> loginMock(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciales históricas del usuario",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Ejemplo Usuario Legacy",
                    value = "{\n  \"correo\": \"maria@gmail.com\",\n  \"password\": \"legacy123\"\n}"
                )
            )
        )
        @RequestBody Map<String, String> request
    ) {
        System.err.println("netro a controller");
        String correo = request.get("correo");
        String password = request.get("password");

        // Simulación sistema legacy
        if ("maria@gmail.com".equals(correo) && "legacy123".equals(password)) {
            return ResponseEntity.ok(Map.of(
                    "id", 1,
                    "nombre", "Maria Legacy",
                    "correo", correo,
                    "rol", "CLIENTE",
                    "mensaje", "Login legacy exitoso"
            ));
        }

        return ResponseEntity.status(401).body(Map.of(
                "mensaje", "Credenciales legacy inválidas"
        ));
    }
}