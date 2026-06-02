package cl.duoc.clientes.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/legacy")
public class LegacyController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginMock(@RequestBody Map<String, String> request) {
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



