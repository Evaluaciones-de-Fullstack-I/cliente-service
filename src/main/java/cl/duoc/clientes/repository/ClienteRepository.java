package cl.duoc.clientes.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.duoc.clientes.repository.*;
import cl.duoc.clientes.model.Cliente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository

public interface  ClienteRepository extends JpaRepository<Cliente, Integer> {
List<Cliente> findByRol(String rol);

    Cliente findByCorreo(String correo);

    boolean existsByCorreo(String correo);



@Query(
    value = "SELECT * FROM clientes WHERE rol = :rol",nativeQuery = true
)
List<Cliente> buscarPorRolCustom(
        @Param("rol") String rol
);
}